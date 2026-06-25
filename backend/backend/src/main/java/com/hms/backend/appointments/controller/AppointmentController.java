package com.hms.backend.appointments.controller;

import com.hms.backend.appointments.dto.AppointmentCreateRequest;
import com.hms.backend.appointments.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> bookAppointment(
            @RequestBody AppointmentCreateRequest request
    ) {
        return ResponseEntity.ok(
                appointmentService.bookAppointment(request)
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        return ResponseEntity.ok(
                appointmentService.getAllAppointments()
        );
    }

    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable Long appointmentId
    ) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentById(appointmentId)
        );
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long appointmentId
    ) {
        return ResponseEntity.ok(
                appointmentService.cancelAppointment(appointmentId)
        );
    }

    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestBody AppointmentCreateRequest request
    ) {
        return ResponseEntity.ok(
                appointmentService.rescheduleAppointment(appointmentId, request)
        );
    }
}

