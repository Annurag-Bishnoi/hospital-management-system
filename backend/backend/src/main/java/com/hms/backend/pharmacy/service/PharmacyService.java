package com.hms.backend.pharmacy.service;

import com.hms.backend.entity.MedicalConcept;
import com.hms.backend.pharmacy.dto.*;
import com.hms.backend.pharmacy.dto.UpdateInventoryRequest;
import com.hms.backend.pharmacy.entity.InventoryItem;
import com.hms.backend.pharmacy.entity.MedicineBatch;
import com.hms.backend.pharmacy.entity.StockAuditLog;
import com.hms.backend.pharmacy.repository.InventoryItemRepository;
import com.hms.backend.pharmacy.repository.MedicineBatchRepository;
import com.hms.backend.pharmacy.repository.StockAuditLogRepository;
import com.hms.backend.prescription.entity.Prescription;
import com.hms.backend.prescription.repository.PrescriptionRepository;
import com.hms.backend.repository.MedicalConceptRepository;
import com.hms.backend.billing.service.BillingService;
import com.hms.backend.billing.dto.BillRequest;
import com.hms.backend.billing.dto.BillItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PharmacyService {

    private final InventoryItemRepository inventoryItemRepository;
    private final MedicineBatchRepository medicineBatchRepository;
    private final StockAuditLogRepository stockAuditLogRepository;
    private final MedicalConceptRepository medicalConceptRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillingService billingService;

    @Transactional
    public InventoryResponse addStock(StockAdditionRequest request, String currentUser) {
        // Find or create Inventory Item based on CIEL Concept
        InventoryItem item = inventoryItemRepository.findByCielConceptId(request.getCielConceptId())
                .orElseGet(() -> {
                    MedicalConcept concept = medicalConceptRepository.findById(request.getCielConceptId())
                            .orElseThrow(() -> new IllegalArgumentException("Invalid CIEL Concept ID"));
                    return inventoryItemRepository.save(InventoryItem.builder()
                            .cielConceptId(concept.getCielId())
                            .medicineName(concept.getConceptName())
                            .totalStock(0)
                            .reorderLevel(50)
                            .build());
                });

        // Create Batch
        MedicineBatch batch = MedicineBatch.builder()
                .inventoryItem(item)
                .batchNumber(request.getBatchNumber())
                .quantity(request.getQuantity())
                .expiryDate(request.getExpiryDate())
                .supplierName(request.getSupplierName())
                .unitPrice(request.getUnitPrice())
                .build();
        medicineBatchRepository.save(batch);

        // Update Total Stock
        item.setTotalStock(item.getTotalStock() + request.getQuantity());
        inventoryItemRepository.save(item);

        // Audit Log
        stockAuditLogRepository.save(StockAuditLog.builder()
                .inventoryItem(item)
                .adjustmentQuantity(request.getQuantity())
                .reason("Purchase - Batch " + request.getBatchNumber())
                .adjustedBy(currentUser)
                .timestamp(LocalDateTime.now())
                .build());

        return mapToResponse(item);
    }

    @Transactional
    public InventoryResponse adjustStock(StockAdjustmentRequest request, String currentUser) {
        InventoryItem item = inventoryItemRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Medicine ID"));

        item.setTotalStock(item.getTotalStock() + request.getQuantity());
        if (item.getTotalStock() < 0) item.setTotalStock(0);
        inventoryItemRepository.save(item);

        stockAuditLogRepository.save(StockAuditLog.builder()
                .inventoryItem(item)
                .adjustmentQuantity(request.getQuantity())
                .reason(request.getReason())
                .adjustedBy(currentUser)
                .timestamp(LocalDateTime.now())
                .build());

        return mapToResponse(item);
    }

        @Transactional
    public InventoryResponse updateInventoryItem(Long id, UpdateInventoryRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Inventory ID"));
        if (request.getMedicineName() != null) item.setMedicineName(request.getMedicineName());
        if (request.getReorderLevel() != null) item.setReorderLevel(request.getReorderLevel());
        return mapToResponse(inventoryItemRepository.save(item));
    }

    @Transactional
    public void deleteInventoryItem(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Inventory ID"));
        inventoryItemRepository.delete(item);
    }

    @Transactional
    public InventoryResponse toggleInventoryStatus(Long id, boolean active) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Inventory ID"));
        item.setIsActive(active);
        return mapToResponse(inventoryItemRepository.save(item));
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<InventoryResponse> getAlerts() {
        List<InventoryItem> lowStockItems = inventoryItemRepository.findLowStockItems();
        return lowStockItems.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<Prescription> getPendingPrescriptions() {
        List<Prescription> pending = prescriptionRepository.findByStatus("CREATED");
        for (Prescription p : pending) {
            for (com.hms.backend.medication.entity.Medication m : p.getMedications()) {
                InventoryItem item = inventoryItemRepository.findByCielConceptId(m.getMedicationCode()).orElse(null);
                if (item != null) {
                    double mockPrice = 15.50 + (item.getMedicineName().length() * 2);
                    java.math.BigDecimal price = java.math.BigDecimal.valueOf(mockPrice);
                    m.setUnitPrice(price);
                    try {
                        int qty = Integer.parseInt(m.getQuantity() != null ? m.getQuantity() : "1");
                        m.setTotalPrice(price.multiply(java.math.BigDecimal.valueOf(qty)));
                    } catch (NumberFormatException e) {
                        m.setTotalPrice(price);
                    }
                } else {
                    // Fallback to 0 if not found
                    m.setUnitPrice(java.math.BigDecimal.ZERO);
                    m.setTotalPrice(java.math.BigDecimal.ZERO);
                }
            }
        }
        return pending;
    }

    public List<Prescription> getDispensedPrescriptions() {
        return prescriptionRepository.findByStatus("DISPENSED");
    }

    @Transactional
    public DispenseResponse dispenseMedicine(DispenseRequest request, String currentUser) {
        Prescription prescription = prescriptionRepository.findById(request.getPrescriptionId())
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));

        if (!"CREATED".equals(prescription.getStatus())) {
            throw new IllegalStateException("Prescription is already " + prescription.getStatus());
        }

        boolean ledgerUpdated = false;

        for (DispenseItemDto itemDto : request.getItems()) {
            InventoryItem inventoryItem = inventoryItemRepository.findByCielConceptId(itemDto.getMedicineId())
                    .orElseThrow(() -> new IllegalArgumentException("Medicine not found in inventory: " + itemDto.getMedicineId()));

            int remainingToDispense = itemDto.getQuantity();

            if (inventoryItem.getTotalStock() < remainingToDispense) {
                throw new IllegalStateException("Insufficient stock for " + inventoryItem.getMedicineName());
            }

            // FIFO Dispensing
            List<MedicineBatch> batches = medicineBatchRepository.findByInventoryItemOrderByExpiryDateAsc(inventoryItem);
            for (MedicineBatch batch : batches) {
                if (remainingToDispense <= 0) break;
                if (batch.getQuantity() > 0) {
                    int dispenseFromBatch = Math.min(batch.getQuantity(), remainingToDispense);
                    batch.setQuantity(batch.getQuantity() - dispenseFromBatch);
                    remainingToDispense -= dispenseFromBatch;
                    medicineBatchRepository.save(batch);
                }
            }

            // Update Total Stock
            inventoryItem.setTotalStock(inventoryItem.getTotalStock() - itemDto.getQuantity());
            inventoryItemRepository.save(inventoryItem);

            // Audit
            stockAuditLogRepository.save(StockAuditLog.builder()
                    .inventoryItem(inventoryItem)
                    .adjustmentQuantity(-itemDto.getQuantity())
                    .reason("Dispensed against Prescription #" + prescription.getPrescriptionId())
                    .adjustedBy(currentUser)
                    .timestamp(LocalDateTime.now())
                    .build());
            
            ledgerUpdated = true;
        }

        prescription.setStatus("DISPENSED");
        prescriptionRepository.save(prescription);

        // Generate a Bill for the Pharmacy items
        BillRequest billRequest = new BillRequest();
        billRequest.setPatientId(prescription.getPatient().getPatientId());
        billRequest.setPatientName(prescription.getPatient().getFullName());
        billRequest.setDepartment("PHARMACY");
        billRequest.setGeneratedBy(currentUser);
        
        List<BillItemRequest> billItems = new ArrayList<>();
        for (DispenseItemDto itemDto : request.getItems()) {
            InventoryItem inventoryItem = inventoryItemRepository.findByCielConceptId(itemDto.getMedicineId()).orElse(null);
            if (inventoryItem != null) {
                BillItemRequest billItemReq = new BillItemRequest();
                billItemReq.setDescription(inventoryItem.getMedicineName());
                billItemReq.setQuantity(itemDto.getQuantity());
                // Using the mock price formula defined in frontend: 15.50 + (name.length * 2)
                double mockPrice = 15.50 + (inventoryItem.getMedicineName().length() * 2);
                billItemReq.setUnitPrice(BigDecimal.valueOf(mockPrice));
                billItems.add(billItemReq);
            }
        }
        billRequest.setItems(billItems);
        billingService.generateBill(billRequest);

        return DispenseResponse.builder()
                .status("DISPENSED")
                .ledgerUpdated(ledgerUpdated)
                .build();
    }

    private InventoryResponse mapToResponse(InventoryItem item) {
        List<MedicineBatch> batches = medicineBatchRepository.findByInventoryItemOrderByExpiryDateAsc(item);
        LocalDate nearestExpiry = batches.stream()
                .filter(b -> b.getQuantity() > 0)
                .map(MedicineBatch::getExpiryDate)
                .findFirst()
                .orElse(null);

        return InventoryResponse.builder()
                .inventoryItemId(item.getId())
                .cielConceptId(item.getCielConceptId())
                .medicineName(item.getMedicineName())
                .totalStock(item.getTotalStock())
                .reorderLevel(item.getReorderLevel())
                .isLowStock(item.getTotalStock() <= item.getReorderLevel())
                .nearestExpiryDate(nearestExpiry)
                .isActive(item.getIsActive())
                .build();
    }
}

