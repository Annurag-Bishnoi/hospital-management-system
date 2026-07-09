package com.hms.backend.visits.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitResponse {
    private Long visitId;
    private Long appointmentId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String diagnosis;
    private String diagnosisCode;
    private String notes;
    private String status;
    private LocalDateTime visitDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private java.util.List<VisitVitalResponse> vitals;
}