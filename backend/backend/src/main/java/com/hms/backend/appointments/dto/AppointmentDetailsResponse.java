package com.hms.backend.appointments.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDetailsResponse {
    private Long appointmentId;
    private String appointmentNumber;
    private String tokenNumber;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reasonForVisit;
    private String status;


    private Long patientId;
    private String patientName;


    private Long doctorId;
    private String doctorName;
}