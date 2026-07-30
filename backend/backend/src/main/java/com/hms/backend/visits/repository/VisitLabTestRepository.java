package com.hms.backend.visits.repository;

import com.hms.backend.visits.entity.VisitLabTest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisitLabTestRepository extends JpaRepository<VisitLabTest, Long> {
    List<VisitLabTest> findByVisitVisitId(Long visitId);
    List<VisitLabTest> findByPatientId(Long patientId);
    List<VisitLabTest> findByStatus(String status);
    List<VisitLabTest> findAllByOrderByRecordedAtDesc();
    long countByStatus(String status);
    List<VisitLabTest> findByBillId(Long billId);
}
