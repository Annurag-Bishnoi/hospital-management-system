package com.hms.backend.patients.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRegisterRequest {

    private String username;

    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private LocalDate dateOfBirth;

    private String gender;

    private String bloodGroup;

    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;

    private String emergencyContactName;

    private String emergencyContactPhone;

    private Long insuranceProviderId;

    private String policyNumber;
}