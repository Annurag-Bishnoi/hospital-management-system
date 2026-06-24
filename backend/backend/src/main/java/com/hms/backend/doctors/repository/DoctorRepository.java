package com.hms.backend.doctors.repository;

import com.hms.backend.doctors.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findByFullNameContainingIgnoreCase(String fullName);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}