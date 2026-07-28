package com.hms.backend.visits.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestResponse {
    private Long testId;
    private Long visitId;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String testCode;
    private String testName;
    private String status;
    private String resultValue;
    private String remarks;
    private String referenceRange;
    private String paymentStatus;
    private LocalDateTime recordedAt;
    private String documentUrl;
    // Appointment info for display
    private Long appointmentId;
}
