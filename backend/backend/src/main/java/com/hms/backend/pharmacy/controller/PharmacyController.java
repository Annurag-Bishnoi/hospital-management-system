package com.hms.backend.pharmacy.controller;

import com.hms.backend.pharmacy.dto.*;
import com.hms.backend.pharmacy.dto.UpdateInventoryRequest;
import com.hms.backend.pharmacy.service.PharmacyService;
import com.hms.backend.prescription.entity.Prescription;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Transactional
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @PutMapping("/inventory/{id}")
    public ResponseEntity<InventoryResponse> updateInventoryItem(@PathVariable Long id, @RequestBody UpdateInventoryRequest request) {
        return ResponseEntity.ok(pharmacyService.updateInventoryItem(id, request));
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<?> deleteInventoryItem(@PathVariable Long id) {
        pharmacyService.deleteInventoryItem(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/inventory/{id}/status")
    public ResponseEntity<InventoryResponse> toggleInventoryStatus(@PathVariable Long id, @RequestParam boolean active) {
        return ResponseEntity.ok(pharmacyService.toggleInventoryStatus(id, active));
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(pharmacyService.getAllInventory());
    }

    @GetMapping("/inventory/{id}/batches")
    public ResponseEntity<List<java.util.Map<String, Object>>> getBatches(@PathVariable Long id) {
        return ResponseEntity.ok(pharmacyService.getBatches(id));
    }

    @GetMapping("/inventory/alerts")
    public ResponseEntity<List<InventoryResponse>> getAlerts() {
        return ResponseEntity.ok(pharmacyService.getAlerts());
    }

    @PostMapping("/inventory/add-batch")
    public ResponseEntity<InventoryResponse> addStock(@RequestBody StockAdditionRequest request) {
        // In a real scenario, fetch username from SecurityContextHolder
        String currentUser = "pharmacist_user"; 
        return ResponseEntity.ok(pharmacyService.addStock(request, currentUser));
    }

    @PutMapping("/inventory/adjust")
    public ResponseEntity<InventoryResponse> adjustStock(@RequestBody StockAdjustmentRequest request) {
        String currentUser = "pharmacist_user"; 
        return ResponseEntity.ok(pharmacyService.adjustStock(request, currentUser));
    }

    @GetMapping("/prescriptions/pending")
    public ResponseEntity<List<java.util.Map<String, Object>>> getPendingPrescriptions() {
        return ResponseEntity.ok(pharmacyService.getPendingPrescriptions().stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList()));
    }

    @GetMapping("/prescriptions/dispensed")
    public ResponseEntity<List<java.util.Map<String, Object>>> getDispensedPrescriptions() {
        return ResponseEntity.ok(pharmacyService.getDispensedPrescriptions().stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList()));
    }

    @GetMapping("/prescriptions/rejected")
    public ResponseEntity<List<java.util.Map<String, Object>>> getRejectedPrescriptions() {
        return ResponseEntity.ok(pharmacyService.getRejectedPrescriptions().stream().map(this::mapToDTO).collect(java.util.stream.Collectors.toList()));
    }

    private java.util.Map<String, Object> mapToDTO(Prescription p) {
        java.util.Map<String, Object> dto = new java.util.HashMap<>();
        dto.put("prescriptionId", p.getPrescriptionId());
        
        java.util.Map<String, Object> patient = new java.util.HashMap<>();
        patient.put("patientId", p.getPatient().getPatientId());
        patient.put("name", p.getPatient().getFullName());
        dto.put("patient", patient);

        java.util.Map<String, Object> doctor = new java.util.HashMap<>();
        doctor.put("doctorId", p.getDoctor().getDoctorId());
        doctor.put("name", p.getDoctor().getFullName());
        dto.put("doctor", doctor);

        dto.put("diagnosis", p.getDiagnosis());
        dto.put("notes", p.getNotes());
        dto.put("status", p.getStatus());
        dto.put("createdAt", p.getCreatedAt());
        dto.put("medications", p.getMedications());
        
        return dto;
    }

    @PostMapping("/dispense")
    public ResponseEntity<DispenseResponse> dispenseMedicine(@RequestBody DispenseRequest request) {
        String currentUser = "pharmacist_user"; 
        return ResponseEntity.ok(pharmacyService.dispenseMedicine(request, currentUser));
    }

    @PutMapping("/prescriptions/{id}/discard")
    public ResponseEntity<?> discardPrescription(@PathVariable Long id) {
        String currentUser = "pharmacist_user";
        pharmacyService.discardPrescription(id, currentUser);
        return ResponseEntity.ok().build();
    }
}

