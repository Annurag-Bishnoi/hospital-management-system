package com.hms.backend.ipd.service;

import com.hms.backend.entity.User;
import com.hms.backend.ipd.dto.IpdDto.*;
import com.hms.backend.ipd.entity.*;
import com.hms.backend.ipd.repository.*;
import com.hms.backend.patients.entity.Patient;
import com.hms.backend.patients.repository.PatientRepository;
import com.hms.backend.repository.UserRepository;
import com.hms.backend.billing.service.BillingService;
import com.hms.backend.billing.dto.BillRequest;
import com.hms.backend.billing.dto.BillItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpdServiceImpl implements IpdService {

    private final WardRepository wardRepository;
    private final BedRepository bedRepository;
    private final AdmissionRepository admissionRepository;
    private final DailyRoundRepository dailyRoundRepository;
    private final NursingChartRepository nursingChartRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final BillingService billingService;

    @Override
    public List<WardDto> getAllWards() {
        return wardRepository.findAll().stream().map(w -> WardDto.builder()
                .id(w.getId())
                .name(w.getName())
                .capacity(w.getCapacity())
                .dailyCharge(w.getDailyCharge())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<BedDto> getAllBeds() {
        return bedRepository.findAll().stream().map(b -> BedDto.builder()
                .id(b.getId())
                .bedNumber(b.getBedNumber())
                .wardName(b.getWard().getName())
                .wardId(b.getWard().getId())
                .status(b.getStatus())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<BedDto> getBedsByWard(Long wardId) {
        return bedRepository.findByWardId(wardId).stream().map(b -> BedDto.builder()
                .id(b.getId())
                .bedNumber(b.getBedNumber())
                .wardName(b.getWard().getName())
                .wardId(b.getWard().getId())
                .status(b.getStatus())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdmissionResponse requestAdmission(AdmissionRequest request, String currentUser) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        User doctor = userRepository.findById(request.getAdmittingDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Admission admission = Admission.builder()
                .patient(patient)
                .admittingDoctor(doctor)
                .admissionDate(LocalDateTime.now())
                .status("REQUESTED")
                .admissionDiagnosis(request.getAdmissionDiagnosis())
                .build();
        return mapToAdmissionResponse(admissionRepository.save(admission));
    }

    @Override
    @Transactional
    public AdmissionResponse assignBed(Long admissionId, AssignBedRequest request, String currentUser) {
        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        Bed bed = bedRepository.findById(request.getBedId())
                .orElseThrow(() -> new RuntimeException("Bed not found"));

        if (!"AVAILABLE".equals(bed.getStatus())) {
            throw new IllegalStateException("Bed is not available");
        }

        bed.setStatus("OCCUPIED");
        bedRepository.save(bed);

        admission.setBed(bed);
        admission.setStatus("ADMITTED");
        return mapToAdmissionResponse(admissionRepository.save(admission));
    }

    @Override
    @Transactional
    public AdmissionResponse dischargePatient(Long admissionId, DischargeRequest request, String currentUser) {
        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));
        
        if (!"ADMITTED".equals(admission.getStatus())) {
            throw new IllegalStateException("Patient is not currently admitted.");
        }

        admission.setStatus("DISCHARGED");
        admission.setDischargeDate(LocalDateTime.now());
        admission.setDischargeSummary(request.getDischargeSummary());
        
        // Free the bed
        Bed bed = admission.getBed();
        if (bed != null) {
            bed.setStatus("AVAILABLE");
            bedRepository.save(bed);
        }

        // Generate IPD Bill
        long daysAdmitted = ChronoUnit.DAYS.between(admission.getAdmissionDate(), admission.getDischargeDate());
        if (daysAdmitted == 0) daysAdmitted = 1; // Minimum 1 day charge
        
        BigDecimal dailyCharge = bed.getWard().getDailyCharge();
        BigDecimal totalRoomCharge = dailyCharge.multiply(BigDecimal.valueOf(daysAdmitted));

        BillRequest ipdBill = new BillRequest();
        ipdBill.setPatientId(admission.getPatient().getPatientId());
        ipdBill.setPatientName(admission.getPatient().getFullName());
        ipdBill.setDepartment("IPD");
        ipdBill.setGeneratedBy(currentUser);

        List<BillItemRequest> items = new ArrayList<>();
        BillItemRequest roomChargeItem = new BillItemRequest();
        roomChargeItem.setDescription(bed.getWard().getName() + " Room Charge (" + daysAdmitted + " Days)");
        roomChargeItem.setQuantity((int) daysAdmitted);
        roomChargeItem.setUnitPrice(dailyCharge);
        items.add(roomChargeItem);

        ipdBill.setItems(items);
        billingService.generateBill(ipdBill);

        return mapToAdmissionResponse(admissionRepository.save(admission));
    }

    @Override
    public List<AdmissionResponse> getAdmissionsByStatus(String status) {
        return admissionRepository.findByStatus(status).stream()
                .map(this::mapToAdmissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AdmissionResponse> getAdmissionsByDoctor(String currentUser) {
        User doctor = userRepository.findByUsername(currentUser)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return admissionRepository.findByAdmittingDoctorUserId(doctor.getUserId()).stream()
                .map(this::mapToAdmissionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DailyRoundResponse addDailyRound(Long admissionId, DailyRoundRequest request, String currentUser) {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow();
        User doctor = userRepository.findByUsername(currentUser).orElseThrow();
        
        DailyRound round = DailyRound.builder()
                .admission(admission)
                .doctor(doctor)
                .roundDate(LocalDateTime.now())
                .clinicalNotes(request.getClinicalNotes())
                .build();
                
        round = dailyRoundRepository.save(round);
        return DailyRoundResponse.builder()
                .id(round.getId())
                .doctorName(doctor.getFullName())
                .roundDate(round.getRoundDate())
                .clinicalNotes(round.getClinicalNotes())
                .build();
    }

    @Override
    public List<DailyRoundResponse> getDailyRounds(Long admissionId) {
        return dailyRoundRepository.findByAdmissionIdOrderByRoundDateDesc(admissionId).stream().map(r -> DailyRoundResponse.builder()
                .id(r.getId())
                .doctorName(r.getDoctor().getFullName())
                .roundDate(r.getRoundDate())
                .clinicalNotes(r.getClinicalNotes())
                .build()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NursingChartResponse addNursingChart(Long admissionId, NursingChartRequest request, String currentUser) {
        Admission admission = admissionRepository.findById(admissionId).orElseThrow();
        User nurse = userRepository.findByUsername(currentUser).orElseThrow();
        
        NursingChart chart = NursingChart.builder()
                .admission(admission)
                .nurse(nurse)
                .recordedAt(LocalDateTime.now())
                .temperature(request.getTemperature())
                .bloodPressure(request.getBloodPressure())
                .heartRate(request.getHeartRate())
                .spo2(request.getSpo2())
                .nursingNotes(request.getNursingNotes())
                .build();
                
        chart = nursingChartRepository.save(chart);
        return NursingChartResponse.builder()
                .id(chart.getId())
                .nurseName(nurse.getFullName())
                .recordedAt(chart.getRecordedAt())
                .temperature(chart.getTemperature())
                .bloodPressure(chart.getBloodPressure())
                .heartRate(chart.getHeartRate())
                .spo2(chart.getSpo2())
                .nursingNotes(chart.getNursingNotes())
                .build();
    }

    @Override
    public List<NursingChartResponse> getNursingCharts(Long admissionId) {
        return nursingChartRepository.findByAdmissionIdOrderByRecordedAtDesc(admissionId).stream().map(c -> NursingChartResponse.builder()
                .id(c.getId())
                .nurseName(c.getNurse().getFullName())
                .recordedAt(c.getRecordedAt())
                .temperature(c.getTemperature())
                .bloodPressure(c.getBloodPressure())
                .heartRate(c.getHeartRate())
                .spo2(c.getSpo2())
                .nursingNotes(c.getNursingNotes())
                .build()).collect(Collectors.toList());
    }

    private AdmissionResponse mapToAdmissionResponse(Admission admission) {
        return AdmissionResponse.builder()
                .id(admission.getId())
                .patientId(admission.getPatient().getPatientId())
                .patientName(admission.getPatient().getFullName())
                .doctorName(admission.getAdmittingDoctor().getFullName())
                .bedNumber(admission.getBed() != null ? admission.getBed().getBedNumber() : null)
                .wardName(admission.getBed() != null ? admission.getBed().getWard().getName() : null)
                .admissionDate(admission.getAdmissionDate())
                .dischargeDate(admission.getDischargeDate())
                .status(admission.getStatus())
                .admissionDiagnosis(admission.getAdmissionDiagnosis())
                .dischargeSummary(admission.getDischargeSummary())
                .build();
    }
}
