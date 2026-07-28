package com.hms.backend.doctors.dto;

import lombok.Data;

@Data
public class DoctorUpdateRequest {

    private String fullName;

    private String department;

    private String phone;

    private String email;

    private Integer experience;

    private String qualifications;

    private String specialization;

    private Double consultationFee;
}