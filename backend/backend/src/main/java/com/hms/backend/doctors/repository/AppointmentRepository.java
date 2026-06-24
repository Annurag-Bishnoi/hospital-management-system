package com.hms.backend.doctors.repository;

import com.hms.backend.doctors.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {
}