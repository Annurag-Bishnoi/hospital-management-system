package com.hms.backend.pharmacy.controller;

import com.hms.backend.pharmacy.dto.*;
import com.hms.backend.pharmacy.dto.UpdateInventoryRequest;
import com.hms.backend.pharmacy.service.PharmacyService;
import com.hms.backend.prescription.entity.Prescription;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
    public ResponseEntity<List<Prescription>> getPendingPrescriptions() {
        return ResponseEntity.ok(pharmacyService.getPendingPrescriptions());
    }

    @PostMapping("/dispense")
    public ResponseEntity<DispenseResponse> dispenseMedicine(@RequestBody DispenseRequest request) {
        String currentUser = "pharmacist_user"; 
        return ResponseEntity.ok(pharmacyService.dispenseMedicine(request, currentUser));
    }
}

