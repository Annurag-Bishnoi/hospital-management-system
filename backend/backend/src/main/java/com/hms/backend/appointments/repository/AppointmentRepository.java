package com.hms.backend.appointments.repository;

import com.hms.backend.appointments.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorDoctorId(Long doctorId);

    List<Appointment> findByPatientPatientId(Long patientId);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findAll();

    boolean existsByDoctorDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );
}
