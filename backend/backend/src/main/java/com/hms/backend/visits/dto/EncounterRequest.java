package com.hms.backend.visits.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncounterRequest {
    private String diagnosis;
    private String diagnosisCode;
    private String notes;
    private java.util.List<com.hms.backend.medication.dto.MedicationDto> medications;
}
