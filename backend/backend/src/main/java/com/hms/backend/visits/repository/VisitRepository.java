package com.hms.backend.visits.repository;

import com.hms.backend.visits.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientPatientId(Long patientId);
    List<Visit> findByDoctorDoctorId(Long doctorId);
    List<Visit> findByAppointmentAppointmentId(Long appointmentId);
}