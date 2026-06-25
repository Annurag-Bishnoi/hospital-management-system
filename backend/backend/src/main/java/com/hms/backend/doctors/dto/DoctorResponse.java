package com.hms.backend.doctors.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorResponse {

    private Long doctorId;

    private String doctorCode;

    private Long userId;

    private String username;

    private String fullName;

    private String department;

    private String phone;

    private String email;

    private Integer experience;

    private Boolean active;
}