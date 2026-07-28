package com.hms.backend.pharmacy.dto;

import lombok.Data;

@Data
public class UpdateInventoryRequest {
    private String medicineName;
    private Integer reorderLevel;
}
