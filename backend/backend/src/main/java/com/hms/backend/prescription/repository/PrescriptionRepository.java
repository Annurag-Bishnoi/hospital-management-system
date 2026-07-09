package com.hms.backend.prescription.repository;

import com.hms.backend.prescription.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByAppointment_AppointmentId(Long appointmentId);
    List<Prescription> findByPatient_PatientId(Long patientId);
    List<Prescription> findByDoctor_DoctorId(Long doctorId);
}
