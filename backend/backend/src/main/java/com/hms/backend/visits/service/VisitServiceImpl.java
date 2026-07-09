package com.hms.backend.visits.service;

import com.hms.backend.appointments.entity.Appointment;
import com.hms.backend.appointments.repository.AppointmentRepository;
import com.hms.backend.doctors.entity.Doctor;
import com.hms.backend.doctors.repository.DoctorRepository;
import com.hms.backend.patients.entity.Patient;
import com.hms.backend.patients.repository.PatientRepository;
import com.hms.backend.visits.dto.EncounterRequest;
import com.hms.backend.visits.dto.HandoverRequest;
import com.hms.backend.visits.dto.VisitRequest;
import com.hms.backend.visits.dto.VisitResponse;
import com.hms.backend.visits.entity.Visit;
import com.hms.backend.visits.repository.VisitRepository;
import com.hms.backend.visits.repository.VisitVitalRepository;
import com.hms.backend.visits.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final VisitVitalRepository visitVitalRepository;
    private final com.hms.backend.repository.MedicalConceptRepository medicalConceptRepository;
    private final com.hms.backend.prescription.repository.PrescriptionRepository prescriptionRepository;

    @Override
    @Transactional
    public VisitResponse createVisit(VisitRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Visit visit = Visit.builder()
                .appointment(appointment)
                .patient(patient)
                .doctor(doctor)
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .status(request.getStatus() != null ? request.getStatus() : "CHECKED_IN")
                .visitDate(request.getVisitDate())
                .build();

        return mapToResponse(visitRepository.save(visit));
    }

    @Override
    @Transactional
    public VisitResponse updateVisit(Long visitId, VisitRequest request) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        // Note: Usually, you shouldn't allow changing the patient/doctor/appointment of an existing visit.
        // But included here for completeness based on the DTO.
        visit.setDiagnosis(request.getDiagnosis());
        visit.setNotes(request.getNotes());
        visit.setVisitDate(request.getVisitDate());
        if (request.getStatus() != null) {
            visit.setStatus(request.getStatus());
        }

        return mapToResponse(visitRepository.save(visit));
    }

    @Override
    @Transactional(readOnly = true)
    public VisitResponse getVisitById(Long visitId) {
        return mapToResponse(visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getAllVisits() {
        return visitRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsByPatientId(Long patientId) {
        return visitRepository.findByPatientPatientId(patientId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsByDoctorId(Long doctorId) {
        return visitRepository.findByDoctorDoctorId(doctorId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisitsByAppointmentId(Long appointmentId) {
        return visitRepository.findByAppointmentAppointmentId(appointmentId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteVisit(Long visitId) {
        if (!visitRepository.existsById(visitId)) {
            throw new RuntimeException("Visit not found");
        }
        visitRepository.deleteById(visitId);
    }

    @Override
    @Transactional
    public VisitResponse handover(HandoverRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + request.getAppointmentId()));

        // Find or create the visit for this appointment
        Visit visit = visitRepository.findByAppointmentAppointmentId(appointment.getAppointmentId())
                .stream().findFirst()
                .orElseGet(() -> Visit.builder()
                        .appointment(appointment)
                        .patient(appointment.getPatient())
                        .doctor(appointment.getDoctor())
                        .visitDate(java.time.LocalDateTime.now())
                        .status("CHECKED_IN")
                        .build());

        // Check vitals exist
        boolean vitalsRecorded = visitVitalRepository.existsByVisit_Appointment_AppointmentId(appointment.getAppointmentId());
        if (!vitalsRecorded) {
            throw new IllegalStateException("Cannot hand over: no vitals recorded for this appointment yet.");
        }

        visit.setStatus("READY_FOR_DOCTOR");
        appointment.setStatus("READY_FOR_DOCTOR");
        appointmentRepository.save(appointment);
        return mapToResponse(visitRepository.save(visit));
    }

    @Override
    @Transactional
    public VisitResponse completeEncounter(Long visitId, EncounterRequest request) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));

        if (request.getDiagnosis() != null) visit.setDiagnosis(request.getDiagnosis());
        if (request.getDiagnosisCode() != null) visit.setDiagnosisCode(request.getDiagnosisCode());
        if (request.getNotes() != null) visit.setNotes(request.getNotes());
        visit.setStatus("COMPLETED");

        Appointment appointment = visit.getAppointment();
        appointment.setStatus("COMPLETED");
        appointmentRepository.save(appointment);

        // Atomic Prescription mapping
        if (request.getMedications() != null && !request.getMedications().isEmpty()) {
            com.hms.backend.prescription.entity.Prescription prescription = prescriptionRepository
                .findByAppointment_AppointmentId(appointment.getAppointmentId())
                .orElseGet(() -> com.hms.backend.prescription.entity.Prescription.builder()
                    .appointment(appointment)
                    .patient(appointment.getPatient())
                    .doctor(appointment.getDoctor())
                    .diagnosis(request.getDiagnosis())
                    .notes(request.getNotes())
                    .build());

            // Validate all medications exist in the medical catalog (CIEL Drug concepts)
            for (com.hms.backend.medication.dto.MedicationDto medDto : request.getMedications()) {
                String medicineName = medDto.getMedicineName();
                if (medicineName == null || medicineName.trim().isEmpty()) {
                    throw new IllegalArgumentException("Medication name cannot be empty");
                }
                List<com.hms.backend.entity.MedicalConcept> matches = medicalConceptRepository
                        .findByConceptClassAndConceptNameContainingIgnoreCase("Drug", medicineName.trim());
                boolean hasExactMatch = matches.stream()
                        .anyMatch(m -> m.getConceptName().equalsIgnoreCase(medicineName.trim()));
                if (!hasExactMatch) {
                    throw new IllegalArgumentException(
                        "Medication '" + medicineName + "' is not registered in the medical catalog. Please select a valid drug from the autocomplete suggestions."
                    );
                }
            }

            java.util.List<com.hms.backend.medication.entity.Medication> medications = request.getMedications().stream().map(medDto -> 
                com.hms.backend.medication.entity.Medication.builder()
                        .prescription(prescription)
                        .medicineName(medDto.getMedicineName().trim())
                        .medicationCode(medDto.getMedicationCode())
                        .dosage(medDto.getDosage())
                        .frequency(medDto.getFrequency())
                        .duration(medDto.getDuration())
                        .quantity(medDto.getQuantity())
                        .instructions(medDto.getInstructions())
                        .build()
            ).collect(Collectors.toList());
            
            prescription.setMedications(medications);
            prescriptionRepository.save(prescription);
        }

        return mapToResponse(visitRepository.save(visit));
    }

    @Override
    @Transactional
    public VisitResponse startConsultation(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));

        Appointment appointment = visit.getAppointment();
        if (!"READY_FOR_DOCTOR".equals(appointment.getStatus()) && !"READY_FOR_CONSULTATION".equals(appointment.getStatus()) && !"IN_CONSULTATION".equals(appointment.getStatus())) {
            throw new IllegalStateException(
                "Cannot start consultation: appointment status is '" + appointment.getStatus() +
                "'. Expected READY_FOR_DOCTOR."
            );
        }

        visit.setStatus("IN_CONSULTATION");
        appointment.setStatus("IN_CONSULTATION");
        appointmentRepository.save(appointment);

        return mapToResponse(visitRepository.save(visit));
    }

    @Override
    @Transactional
    public VisitResponse startConsultationByAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        Visit visit = visitRepository.findByAppointmentAppointmentId(appointmentId)
                .stream().findFirst()
                .orElseGet(() -> visitRepository.save(Visit.builder()
                        .appointment(appointment)
                        .patient(appointment.getPatient())
                        .doctor(appointment.getDoctor())
                        .visitDate(java.time.LocalDateTime.now())
                        .status("CHECKED_IN")
                        .build()));

        visit.setStatus("IN_CONSULTATION");
        appointment.setStatus("IN_CONSULTATION");
        appointmentRepository.save(appointment);

        return mapToResponse(visitRepository.save(visit));
    }

    private VisitResponse mapToResponse(Visit visit) {
        List<com.hms.backend.visits.entity.VisitVital> rawVitals = visitVitalRepository.findByVisitVisitId(visit.getVisitId());
        List<com.hms.backend.visits.dto.VisitVitalResponse> mappedVitals = rawVitals.stream().map(vital -> {
            String conceptName = medicalConceptRepository.findByCielId(vital.getCielId())
                    .map(com.hms.backend.entity.MedicalConcept::getConceptName)
                    .orElse("Unknown Concept");

            return com.hms.backend.visits.dto.VisitVitalResponse.builder()
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

        return VisitResponse.builder()
                .visitId(visit.getVisitId())
                .appointmentId(visit.getAppointment().getAppointmentId())
                .patientId(visit.getPatient().getPatientId())
                .patientName(visit.getPatient().getFullName())
                .doctorId(visit.getDoctor().getDoctorId())
                .doctorName(visit.getDoctor().getFullName())
                .diagnosis(visit.getDiagnosis())
                .diagnosisCode(visit.getDiagnosisCode())
                .notes(visit.getNotes())
                .status(visit.getStatus())
                .visitDate(visit.getVisitDate())
                .createdAt(visit.getCreatedAt())
                .updatedAt(visit.getUpdatedAt())
                .vitals(mappedVitals)
                .build();
    }
}