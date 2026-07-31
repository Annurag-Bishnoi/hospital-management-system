package com.hms.backend.billing.controller;

import com.hms.backend.billing.dto.BillRequest;
import com.hms.backend.billing.dto.BillResponse;
import com.hms.backend.billing.dto.PaymentRequest;
import com.hms.backend.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/generate")
    public ResponseEntity<BillResponse> generateBill(@RequestBody BillRequest request) {
        return ResponseEntity.ok(billingService.generateBill(request));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BillResponse> processPayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(billingService.processPayment(id, request));
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<BillResponse>> getAllBills(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(billingService.getAllBills(status));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillResponse>> getPatientBills(@PathVariable Long patientId) {
        return ResponseEntity.ok(billingService.getBillsByPatient(patientId));
    }
}
