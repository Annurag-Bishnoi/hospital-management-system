package com.hms.backend.billing.repository;

import com.hms.backend.billing.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByPatientId(Long patientId);
    List<Bill> findByStatusOrderByCreatedAtDesc(String status);
    List<Bill> findAllByOrderByCreatedAtDesc();
}
