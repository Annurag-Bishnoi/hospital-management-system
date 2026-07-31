package com.hms.backend.repository;

import com.hms.backend.entity.MedicalConcept;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalConceptRepository extends JpaRepository<MedicalConcept, String> {


    // 1. Search API for the UI (Filters by class "Test" and searches by name)
    List<MedicalConcept> findByConceptClassAndConceptNameContainingIgnoreCase(String conceptClass, String conceptName);

    // 2. Helper to get the concept name when returning VisitVital responses
    Optional<MedicalConcept> findByCielId(String cielId);

    // 3. General name search
    List<MedicalConcept> findByConceptNameContainingIgnoreCase(String conceptName);

    // 4. Check if class exists
    boolean existsByConceptClass(String conceptClass);
}
