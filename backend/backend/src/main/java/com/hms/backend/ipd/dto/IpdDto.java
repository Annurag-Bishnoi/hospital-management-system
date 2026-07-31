package com.hms.backend.ipd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IpdDto {

    @Data
    @Builder
    public static class WardDto {
        private Long id;
        private String name;
        private int capacity;
        private BigDecimal dailyCharge;
    }

    @Data
    @Builder
    public static class BedDto {
        private Long id;
        private String bedNumber;
        private String wardName;
        private Long wardId;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdmissionRequest {
        private Long patientId;
        private Long admittingDoctorId;
        private String admissionDiagnosis;
    }

    @Data
    @Builder
    public static class AdmissionResponse {
        private Long id;
        private Long patientId;
        private String patientName;
        private String doctorName;
        private String bedNumber;
        private String wardName;
        private LocalDateTime admissionDate;
        private LocalDateTime dischargeDate;
        private String status;
        private String admissionDiagnosis;
        private String dischargeSummary;
    }

    @Data
    public static class AssignBedRequest {
        private Long bedId;
    }
    
    @Data
    public static class DischargeRequest {
        private String dischargeSummary;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRoundRequest {
        private String clinicalNotes;
    }

    @Data
    @Builder
    public static class DailyRoundResponse {
        private Long id;
        private String doctorName;
        private LocalDateTime roundDate;
        private String clinicalNotes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NursingChartRequest {
        private Double temperature;
        private String bloodPressure;
        private Integer heartRate;
        private Integer spo2;
        private String nursingNotes;
    }

    @Data
    @Builder
    public static class NursingChartResponse {
        private Long id;
        private String nurseName;
        private LocalDateTime recordedAt;
        private Double temperature;
        private String bloodPressure;
        private Integer heartRate;
        private Integer spo2;
        private String nursingNotes;
    }
}
