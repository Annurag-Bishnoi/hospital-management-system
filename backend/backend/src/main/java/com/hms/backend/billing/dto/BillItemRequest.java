package com.hms.backend.billing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillItemRequest {
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
}
