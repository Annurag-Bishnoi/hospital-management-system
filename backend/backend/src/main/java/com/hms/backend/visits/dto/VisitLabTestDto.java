package com.hms.backend.visits.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLabTestDto {
    private Long testId;
    private String testCode;
    private String testName;
    private String status;
    private String paymentStatus;
    private String referenceRange;
    private String resultValue;
    private LocalDateTime recordedAt;
    private String documentUrl;
}
