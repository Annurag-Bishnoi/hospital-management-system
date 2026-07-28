package com.hms.backend.patients.repository;

import com.hms.backend.patients.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientCode(String patientCode);

    Optional<Patient> findByUserUserId(Long userId);

    boolean existsByUserUserId(Long userId);

    List<Patient> findByFullNameContainingIgnoreCaseOrPhoneContainingOrPatientCodeContainingIgnoreCase(
            String fullName,
            String phone,
            String patientCode
    );

    long countByActive(Boolean active);
}