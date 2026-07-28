package com.hms.backend.doctors.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorStatusRequest {

    @NotNull(message = "Status is required")
    private Boolean active;
}
