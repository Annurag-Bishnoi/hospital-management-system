package com.hms.backend.billing.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillItemResponse {
    private Long id;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
