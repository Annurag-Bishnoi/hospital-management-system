package com.hms.backend.visits.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitRequest {
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    private String diagnosis;
    private String diagnosisCode;
    private String notes;
    private String status;

    @NotNull(message = "Visit date is required")
    private LocalDateTime visitDate;
}