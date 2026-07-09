package com.hms.backend.visits.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitVitalResponse {
    private Long vitalId;
    private Long visitId;
    private Long patientId;

    private Long cielId;
    private String conceptName; // Fetched from MedicalConcept for easy UI display

    private String vitalValue;
    private String unit;
    private LocalDateTime recordedAt;
}