package com.hms.backend.users.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {

    private Long userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String roleCode;
    private String roleName;
    private Boolean active;
    private Boolean accountLocked;

    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private String temporaryPassword;
}