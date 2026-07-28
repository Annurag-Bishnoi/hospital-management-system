package com.hms.backend.billing.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BillRequest {
    private Long patientId;
    private String patientName;
    private String department;
    private String generatedBy;
    private List<BillItemRequest> items;
}
