package com.hms.backend.lab.repository;

import com.hms.backend.lab.entity.LabTestMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabTestMasterRepository extends JpaRepository<LabTestMaster, Long> {
    Optional<LabTestMaster> findByCielConceptId(String cielConceptId);
    boolean existsByCielConceptId(String cielConceptId);
}
