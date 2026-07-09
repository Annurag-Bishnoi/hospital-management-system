package com.hms.backend.visits.service;

import com.hms.backend.appointments.entity.Appointment;
import com.hms.backend.appointments.repository.AppointmentRepository;
import com.hms.backend.entity.MedicalConcept;
import com.hms.backend.repository.MedicalConceptRepository;
import com.hms.backend.visits.dto.SimpleVitalsRequest;
import com.hms.backend.visits.dto.SimpleVitalsResponse;
import com.hms.backend.visits.dto.VisitVitalRequest;
import com.hms.backend.visits.dto.VisitVitalResponse;
import com.hms.backend.visits.entity.Visit;
import com.hms.backend.visits.entity.VisitVital;
import com.hms.backend.visits.repository.VisitRepository;
import com.hms.backend.visits.repository.VisitVitalRepository;
import com.hms.backend.visits.service.VisitVitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitVitalServiceImpl implements VisitVitalService {

    private final VisitVitalRepository visitVitalRepository;
    private final VisitRepository visitRepository;
    private final MedicalConceptRepository medicalConceptRepository;
    private final AppointmentRepository appointmentRepository;

    // CIEL IDs for common vitals (matches your database seeder)
    private static final long CIEL_SYSTOLIC_BP    = 5085L;
    private static final long CIEL_DIASTOLIC_BP   = 5086L;
    private static final long CIEL_HEART_RATE     = 5087L;
    private static final long CIEL_TEMPERATURE    = 5088L;
    private static final long CIEL_RESPIRATORY    = 5242L;
    private static final long CIEL_WEIGHT         = 5089L;

    @Override
    @Transactional
    public SimpleVitalsResponse recordSimpleVitals(Long appointmentId, SimpleVitalsRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        // Find or auto-create the Visit for this appointment
        Visit visit = visitRepository.findByAppointmentAppointmentId(appointmentId)
                .stream().findFirst()
                .orElseGet(() -> visitRepository.save(Visit.builder()
                        .appointment(appointment)
                        .patient(appointment.getPatient())
                        .doctor(appointment.getDoctor())
                        .visitDate(java.time.LocalDateTime.now())
                        .status("CHECKED_IN")
                        .build()));

        // Build CIEL-based vital records for each field
        List<VisitVital> vitals = new ArrayList<>();

        if (request.getSystolicBP() != null) {
            vitals.add(buildVital(visit, CIEL_SYSTOLIC_BP, String.valueOf(request.getSystolicBP()), "mmHg"));
        }
        if (request.getDiastolicBP() != null) {
            vitals.add(buildVital(visit, CIEL_DIASTOLIC_BP, String.valueOf(request.getDiastolicBP()), "mmHg"));
        }
        if (request.getHeartRate() != null) {
            vitals.add(buildVital(visit, CIEL_HEART_RATE, String.valueOf(request.getHeartRate()), "bpm"));
        }
        if (request.getTemperature() != null) {
            vitals.add(buildVital(visit, CIEL_TEMPERATURE, String.valueOf(request.getTemperature()), "°F"));
        }
        if (request.getRespiratoryRate() != null) {
            vitals.add(buildVital(visit, CIEL_RESPIRATORY, String.valueOf(request.getRespiratoryRate()), "breaths/min"));
        }
        if (request.getWeight() != null) {
            vitals.add(buildVital(visit, CIEL_WEIGHT, String.valueOf(request.getWeight()), "kg"));
        }

        if (!vitals.isEmpty()) {
            visitVitalRepository.saveAll(vitals);
        }

        // Transition appointment status to READY_FOR_DOCTOR
        appointment.setStatus("READY_FOR_DOCTOR");
        visit.setStatus("READY_FOR_DOCTOR");
        appointmentRepository.save(appointment);
        visitRepository.save(visit);

        String bp = (request.getSystolicBP() != null && request.getDiastolicBP() != null)
                ? request.getSystolicBP() + "/" + request.getDiastolicBP()
                : null;

        return SimpleVitalsResponse.builder()
                .visitId(visit.getVisitId())
                .appointmentId(appointmentId)
                .patientId(visit.getPatient().getPatientId())
                .appointmentStatus("READY_FOR_DOCTOR")
                .bloodPressure(bp)
                .heartRate(request.getHeartRate())
                .temperature(request.getTemperature())
                .respiratoryRate(request.getRespiratoryRate())
                .weight(request.getWeight())
                .notes(request.getNotes())
                .message("Vitals recorded. Patient is now READY FOR DOCTOR.")
                .build();
    }

    private VisitVital buildVital(Visit visit, long cielId, String value, String unit) {
        return VisitVital.builder()
                .visit(visit)
                .patientId(visit.getPatient().getPatientId())
                .cielId(cielId)
                .vitalValue(value)
                .unit(unit)
                .build();
    }

    @Override
    @Transactional
    public List<VisitVitalResponse> recordVitals(Long visitId, List<VisitVitalRequest> requests) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        List<VisitVital> vitalsToSave = new ArrayList<>();
        for (VisitVitalRequest req : requests) {
            VisitVital vital = VisitVital.builder()
                    .visit(visit)
                    .patientId(visit.getPatient().getPatientId()) // Link patient via Visit
                    .cielId(req.getCielId())
                    .vitalValue(req.getVitalValue())
                    .unit(req.getUnit())
                    .build();
            vitalsToSave.add(vital);
        }

        List<VisitVital> savedVitals = visitVitalRepository.saveAll(vitalsToSave);
        return mapToResponseList(savedVitals);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitVitalResponse> getVitalsByVisitId(Long visitId) {
        List<VisitVital> vitals = visitVitalRepository.findByVisitVisitId(visitId);
        return mapToResponseList(vitals);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalConcept> searchVitalConcepts(String searchTerm) {
        // STRICT PATTERN: Filter by concept class "Test" as identified in your CIEL seeder
        return medicalConceptRepository.findByConceptClassAndConceptNameContainingIgnoreCase("Test", searchTerm);
    }

    // Helper to map entities to DTOs and resolve the CIEL concept name
    private List<VisitVitalResponse> mapToResponseList(List<VisitVital> vitals) {
        return vitals.stream().map(vital -> {
            String conceptName = medicalConceptRepository.findByCielId(vital.getCielId())
                    .map(MedicalConcept::getConceptName)
                    .orElse("Unknown Concept");

            return VisitVitalResponse.builder()
                    .vitalId(vital.getVitalId())
                    .visitId(vital.getVisit().getVisitId())
                    .patientId(vital.getPatientId())
                    .cielId(vital.getCielId())
                    .conceptName(conceptName)
                    .vitalValue(vital.getVitalValue())
                    .unit(vital.getUnit())
                    .recordedAt(vital.getRecordedAt())
                    .build();
        }).collect(Collectors.toList());
    }
}