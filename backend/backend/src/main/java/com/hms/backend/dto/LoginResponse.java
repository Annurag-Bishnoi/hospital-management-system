package com.hms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Boolean success;
    private String message;

    private Long userId;
    private String username;
    private String fullName;
    private String role;
}