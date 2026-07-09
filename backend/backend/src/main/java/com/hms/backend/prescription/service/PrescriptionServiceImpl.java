package com.hms.backend.prescription.service;

import com.hms.backend.appointments.entity.Appointment;
import com.hms.backend.appointments.repository.AppointmentRepository;
import com.hms.backend.medication.dto.MedicationDto;
import com.hms.backend.medication.entity.Medication;
import com.hms.backend.prescription.dto.PrescriptionRequest;
import com.hms.backend.prescription.dto.PrescriptionResponse;
import com.hms.backend.prescription.entity.Prescription;
import com.hms.backend.prescription.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final com.hms.backend.repository.MedicalConceptRepository medicalConceptRepository;

    @Override
    @Transactional
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + request.getAppointmentId()));

        if (prescriptionRepository.findByAppointment_AppointmentId(request.getAppointmentId()).isPresent()) {
            throw new RuntimeException("Prescription already exists for this appointment");
        }

        Prescription prescription = Prescription.builder()
                .appointment(appointment)
                .patient(appointment.getPatient())
                .doctor(appointment.getDoctor())
                .diagnosis(request.getDiagnosis())
                .notes(request.getNotes())
                .build();

        if (request.getMedications() != null && !request.getMedications().isEmpty()) {
            // Validate all medications exist in the medical catalog (CIEL Drug concepts)
            for (MedicationDto medDto : request.getMedications()) {
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

            List<Medication> medications = request.getMedications().stream().map(medDto -> 
                Medication.builder()
                        .prescription(prescription)
                        .medicineName(medDto.getMedicineName().trim())
                        .dosage(medDto.getDosage())
                        .frequency(medDto.getFrequency())
                        .duration(medDto.getDuration())
                        .instructions(medDto.getInstructions())
                        .build()
            ).collect(Collectors.toList());
            
            prescription.setMedications(medications);
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        return mapToResponse(savedPrescription);
    }

    @Override
    public PrescriptionResponse getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found with id: " + id));
        return mapToResponse(prescription);
    }

    @Override
    public PrescriptionResponse getPrescriptionByAppointmentId(Long appointmentId) {
        Prescription prescription = prescriptionRepository.findByAppointment_AppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Prescription not found for appointment id: " + appointmentId));
        return mapToResponse(prescription);
    }

    @Override
    public List<PrescriptionResponse> getPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findByPatient_PatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrescriptionResponse> getPrescriptionsByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctor_DoctorId(doctorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrescriptionResponse mapToResponse(Prescription prescription) {
        List<MedicationDto> medicationDtos = Collections.emptyList();
        if (prescription.getMedications() != null) {
            medicationDtos = prescription.getMedications().stream().map(med ->
                    MedicationDto.builder()
                            .medicationId(med.getMedicationId())
                            .medicineName(med.getMedicineName())
                            .dosage(med.getDosage())
                            .frequency(med.getFrequency())
                            .duration(med.getDuration())
                            .instructions(med.getInstructions())
                            .build()
            ).collect(Collectors.toList());
        }

        return PrescriptionResponse.builder()
                .prescriptionId(prescription.getPrescriptionId())
                .appointmentId(prescription.getAppointment().getAppointmentId())
                .patientId(prescription.getPatient().getPatientId())
                .doctorId(prescription.getDoctor().getDoctorId())
                .patientName(prescription.getPatient().getFullName())
                .doctorName(prescription.getDoctor().getFullName())
                .diagnosis(prescription.getDiagnosis())
                .notes(prescription.getNotes())
                .medications(medicationDtos)
                .createdAt(prescription.getCreatedAt())
                .build();
    }
}
