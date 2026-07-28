package com.hms.backend.billing.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String processedBy; // e.g. "Accountant123"
    private java.math.BigDecimal taxPercentage;
    private java.math.BigDecimal discountAmount;
    private java.math.BigDecimal insuranceCoverageAmount;
}
