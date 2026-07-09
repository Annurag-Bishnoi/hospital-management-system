package com.hms.backend.visits.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Accepts a single structured vitals record from the frontend.
 * Replaces the old CIEL-based List<VisitVitalRequest> approach for the
 * receptionist workflow, fixing HttpMessageNotReadableException.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleVitalsRequest {

    // Systolic BP: clinically valid 60-250 mmHg
    @Min(value = 60, message = "Systolic BP must be at least 60 mmHg")
    @Max(value = 250, message = "Systolic BP cannot exceed 250 mmHg")
    private Integer systolicBP;

    // Diastolic BP: clinically valid 40-150 mmHg
    @Min(value = 40, message = "Diastolic BP must be at least 40 mmHg")
    @Max(value = 150, message = "Diastolic BP cannot exceed 150 mmHg")
    private Integer diastolicBP;

    // Heart rate: 30-250 bpm
    @Min(value = 30, message = "Heart rate must be at least 30 bpm")
    @Max(value = 250, message = "Heart rate cannot exceed 250 bpm")
    private Integer heartRate;

    // Temperature in °F: 90.0 - 110.0
    @DecimalMin(value = "90.0", message = "Temperature must be at least 90°F")
    @DecimalMax(value = "110.0", message = "Temperature cannot exceed 110°F")
    private Double temperature;

    // Respiratory rate: 4-60 breaths/min
    @Min(value = 4, message = "Respiratory rate must be at least 4")
    @Max(value = 60, message = "Respiratory rate cannot exceed 60")
    private Integer respiratoryRate;

    // Weight in kg: 1-300
    @DecimalMin(value = "1.0", message = "Weight must be at least 1 kg")
    @DecimalMax(value = "300.0", message = "Weight cannot exceed 300 kg")
    private Double weight;

    private String notes;
}
