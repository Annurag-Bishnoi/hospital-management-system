package com.hms.backend.appointments.controller;

import com.hms.backend.appointments.dto.AppointmentCreateRequest;
import com.hms.backend.appointments.dto.AppointmentDetailsResponse;
import com.hms.backend.appointments.dto.AppointmentResponse;
import com.hms.backend.appointments.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/register")
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @RequestBody AppointmentCreateRequest request
    ) {
        return ResponseEntity.ok(
                appointmentService.bookAppointment(request)
        );
    }

    @GetMapping("/get")
    public ResponseEntity<List<AppointmentDetailsResponse>> getAllAppointments() {
        return ResponseEntity.ok(
                appointmentService.getAllAppointments()
        );
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentDetailsResponse> getAppointmentById(
            @PathVariable Long appointmentId
    ) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentById(appointmentId)
        );
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long appointmentId
    ) {
        return ResponseEntity.ok(
                appointmentService.cancelAppointment(appointmentId)
        );
    }

    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestBody AppointmentCreateRequest request
    ) {
        return ResponseEntity.ok(
                appointmentService.rescheduleAppointment(appointmentId, request)
        );
    }
}