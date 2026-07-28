package com.hms.backend.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockAdditionRequest {
    private String cielConceptId;
    private String batchNumber;
    private Integer quantity;
    private LocalDate expiryDate;
    private String supplierName;
    private BigDecimal unitPrice;
}
