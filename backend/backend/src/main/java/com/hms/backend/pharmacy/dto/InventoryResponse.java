package com.hms.backend.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    private Long inventoryItemId;
    private String cielConceptId;
    private String medicineName;
    private Integer totalStock;
    private Integer reorderLevel;
    private Boolean isLowStock;
    private LocalDate nearestExpiryDate;
    private Boolean isActive;
}
