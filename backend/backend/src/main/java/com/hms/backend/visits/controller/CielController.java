package com.hms.backend.visits.controller;

import com.hms.backend.entity.MedicalConcept;
import com.hms.backend.repository.MedicalConceptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ciel")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CielController {

    private final MedicalConceptRepository medicalConceptRepository;

    /**
     * Debugging: Get total count of medical concepts
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getCount() {
        return ResponseEntity.ok(medicalConceptRepository.count());
    }

    /**
     * Unified CIEL Dictionary Autocomplete Search
     * GET /api/ciel/search?q={searchTerm}&type={diagnosis/medication}
     */
    @GetMapping("/search")
    public ResponseEntity<List<MedicalConcept>> searchCiel(
            @RequestParam("q") String q,
            @RequestParam("type") String type) {

        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        String searchTerm = q.trim();
        List<MedicalConcept> results = new ArrayList<>();

        if ("medication".equalsIgnoreCase(type)) {
            // Search drugs, wrap in ArrayList to make mutable
            results = new ArrayList<>(medicalConceptRepository
                    .findTop50ByConceptClassAndConceptNameStartingWithIgnoreCase("Drug", searchTerm));
        } else if ("diagnosis".equalsIgnoreCase(type)) {
            // Search all classes EXCEPT Drug and Test
            List<MedicalConcept> allMatches = medicalConceptRepository
                    .findTop50ByConceptNameStartingWithIgnoreCase(searchTerm);
            for (MedicalConcept concept : allMatches) {
                String cls = concept.getConceptClass();
                if (!"Drug".equals(cls) && !"Test".equals(cls)) {
                    results.add(concept);
                }
            }
        } else if ("test".equalsIgnoreCase(type)) {
            // Search lab tests, wrap in ArrayList to make mutable
            results = new ArrayList<>(medicalConceptRepository
                    .findTop50ByConceptClassAndConceptNameStartingWithIgnoreCase("Test", searchTerm));
        }

        // Limit results to 50 for quick rendering
        if (results.size() > 50) {
            results = results.subList(0, 50);
        }

        return ResponseEntity.ok(results);
    }
}
