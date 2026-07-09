package com.hms.backend.prescription.service;

import com.hms.backend.prescription.dto.PrescriptionRequest;
import com.hms.backend.prescription.dto.PrescriptionResponse;

import java.util.List;

public interface PrescriptionService {
    PrescriptionResponse createPrescription(PrescriptionRequest request);
    PrescriptionResponse getPrescriptionById(Long id);
    PrescriptionResponse getPrescriptionByAppointmentId(Long appointmentId);
    List<PrescriptionResponse> getPrescriptionsByPatientId(Long patientId);
    List<PrescriptionResponse> getPrescriptionsByDoctorId(Long doctorId);
}
