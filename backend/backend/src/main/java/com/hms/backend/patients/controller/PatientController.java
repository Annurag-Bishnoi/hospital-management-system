package com.hms.backend.patients.controller;

import com.hms.backend.patients.dto.PatientRegisterRequest;
import com.hms.backend.patients.dto.PatientStatusRequest;
import com.hms.backend.patients.dto.PatientUpdateRequest;
import com.hms.backend.patients.dto.PatientAllergyRequest;
import com.hms.backend.patients.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegisterRequest request) {
        try {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(patientService.registerPatient(request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<?> getPatientById(@PathVariable Long patientId) {
        try {
            return ResponseEntity.ok(patientService.getPatientById(patientId));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPatientByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(patientService.getPatientByUserId(userId));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPatients(@RequestParam String keyword) {
        return ResponseEntity.ok(patientService.searchPatients(keyword));
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientUpdateRequest request
    ) {
        try {
            return ResponseEntity.ok(patientService.updatePatient(patientId, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/{patientId}/status")
    public ResponseEntity<?> updatePatientStatus(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientStatusRequest request
    ) {
        try {
            return ResponseEntity.ok(patientService.updatePatientStatus(patientId, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{patientId}/reset-credentials")
    public ResponseEntity<?> resetCredentials(@PathVariable Long patientId) {
        try {
            return ResponseEntity.ok(patientService.resetCredentials(patientId));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{patientId}/allergies")
    public ResponseEntity<?> addAllergy(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientAllergyRequest request
    ) {
        try {
            return ResponseEntity.ok(patientService.addAllergyToPatient(patientId, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}