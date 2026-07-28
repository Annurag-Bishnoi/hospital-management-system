package com.hms.backend.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BillResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private String department;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal taxPercentage;
    private BigDecimal discountAmount;
    private BigDecimal insuranceCoverageAmount;
    private BigDecimal patientPayableAmount;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String generatedBy;
    private String processedBy;
    private List<BillItemResponse> items;
}
