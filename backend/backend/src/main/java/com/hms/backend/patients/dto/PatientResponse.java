package com.hms.backend.patients.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PatientResponse {

    private Long patientId;
    private String patientCode;

    private Long userId;
    private String username;

    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;

    private String phone;
    private String email;
    private String address;

    private Long insuranceProviderId;
    private String insuranceProviderName;
    private String policyNumber;

    private String emergencyContactName;
    private String emergencyContactPhone;

    private Boolean active;

    private java.util.List<AllergyDto> allergies;

    @Getter
    @Setter
    @Builder
    public static class AllergyDto {
        private Long id;
        private String allergyName;
        private String severity;
        private String notes;
    }
}