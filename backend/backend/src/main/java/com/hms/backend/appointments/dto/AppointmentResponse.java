package com.hms.backend.appointments.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppointmentResponse {

    private Long appointmentId;

    private String appointmentNumber;

    private String tokenNumber;

    private String status;

    private String message;
}