package com.hms.backend.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockAdjustmentRequest {
    private Long medicineId; // refers to InventoryItem ID
    private Integer quantity; // positive for addition, negative for reduction
    private String reason;
}
