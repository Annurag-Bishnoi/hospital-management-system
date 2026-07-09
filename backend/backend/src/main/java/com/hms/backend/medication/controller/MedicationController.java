package com.hms.backend.medication.controller;

import com.hms.backend.entity.MedicalConcept;
import com.hms.backend.repository.MedicalConceptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicationController {

    private final MedicalConceptRepository medicalConceptRepository;

    /**
     * Search medications (cielId/conceptName/conceptClass = "Drug") in CIEL Dictionary
     * GET /api/medications/search?query={searchTerm}
     */
    @GetMapping("/search")
    public ResponseEntity<List<MedicalConcept>> searchMedications(@RequestParam String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }
        List<MedicalConcept> drugs = medicalConceptRepository
                .findByConceptClassAndConceptNameContainingIgnoreCase("Drug", query.trim());
        return ResponseEntity.ok(drugs);
    }
}
