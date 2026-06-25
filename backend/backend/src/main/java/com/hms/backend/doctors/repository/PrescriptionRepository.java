package com.hms.backend.doctors.repository;

import com.hms.backend.doctors.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {
}