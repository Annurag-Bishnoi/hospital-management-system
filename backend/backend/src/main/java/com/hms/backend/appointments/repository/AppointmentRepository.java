package com.hms.backend.appointments.repository;

import com.hms.backend.appointments.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorDoctorId(Long doctorId);

    List<Appointment> findByPatientPatientId(Long patientId);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    @Query("""
            SELECT a FROM Appointment a
            JOIN FETCH a.patient p
            LEFT JOIN FETCH p.user
            JOIN FETCH a.doctor d
            LEFT JOIN FETCH d.user
            """)
    List<Appointment> findAllAppointmentsWithDetails();

    boolean existsByDoctorDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );

    boolean existsByDoctorDoctorIdAndAppointmentDateAndAppointmentTimeAndAppointmentIdNot(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Long appointmentId
    );
}