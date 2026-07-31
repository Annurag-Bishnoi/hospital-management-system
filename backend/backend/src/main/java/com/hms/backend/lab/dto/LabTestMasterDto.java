package com.hms.backend.lab.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LabTestMasterDto {
    private Long id;
    private String cielConceptId;
    private String testName;
    private String conceptClass;
    private BigDecimal unitPrice;
    private Boolean active;
    private LocalDateTime createdAt;
}
