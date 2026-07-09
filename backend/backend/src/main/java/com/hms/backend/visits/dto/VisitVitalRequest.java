package com.hms.backend.visits.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitVitalRequest {
    @NotNull(message = "CIEL ID is required")
    private Long cielId;

    @NotBlank(message = "Vital value is required")
    private String vitalValue;

    private String unit; // Optional, can be derived from CIEL if you store units there
}