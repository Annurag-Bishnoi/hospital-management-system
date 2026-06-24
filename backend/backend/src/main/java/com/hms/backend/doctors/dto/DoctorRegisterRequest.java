package com.hms.backend.doctors.dto;

import lombok.Data;

@Data
public class DoctorRegisterRequest {

    private String fullName;

    private String department;

    private String phone;

    private String email;

    private Integer experience;

    private String username;

    private String password;
}