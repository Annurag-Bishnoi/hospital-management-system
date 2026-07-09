package com.hms.backend.medication.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDto {
    private Long medicationId;

    @NotBlank(message = "Medicine name is required")
    private String medicineName;

    @NotBlank(message = "Dosage is required (e.g., 500mg)")
    private String dosage;

    @NotBlank(message = "Frequency is required (e.g., 1-0-1)")
    private String frequency;

    @NotBlank(message = "Duration is required (e.g., 5 days)")
    private String duration;

    private String instructions;
    private String medicationCode;
    private String quantity;
}
