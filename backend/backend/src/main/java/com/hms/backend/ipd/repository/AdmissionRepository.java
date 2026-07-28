package com.hms.backend.ipd.repository;

import com.hms.backend.ipd.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    List<Admission> findByStatus(String status);
    List<Admission> findByPatientPatientId(Long patientId);
    List<Admission> findByAdmittingDoctorUserId(Long doctorId);
    List<Admission> findByBedWardId(Long wardId);
}
