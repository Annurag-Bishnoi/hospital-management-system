package com.hms.backend.visits.dto;

import lombok.*;

/**
 * Response DTO for the simple vitals endpoint.
 * Returns a formatted summary of what was recorded.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleVitalsResponse {
    private Long visitId;
    private Long appointmentId;
    private Long patientId;
    private String appointmentStatus; // Will be READY_FOR_DOCTOR after handover
    private String bloodPressure;     // formatted "120/80"
    private Integer heartRate;
    private Double temperature;
    private Integer respiratoryRate;
    private Double weight;
    private String notes;
    private String message;
}
