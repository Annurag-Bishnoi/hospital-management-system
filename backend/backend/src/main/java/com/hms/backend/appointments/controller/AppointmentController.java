package com.hms.backend.appointments.controller;

import com.hms.backend.appointments.dto.AppointmentCreateRequest;
import com.hms.backend.appointments.dto.AppointmentDetailsResponse;
import com.hms.backend.appointments.dto.AppointmentResponse;
import com.hms.backend.appointments.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @PutMapping("/{appointmentId}/pay")
    public ResponseEntity<AppointmentResponse> markPaymentPaid(
            @PathVariable Long appointmentId
    ) {
        return ResponseEntity.ok(
                appointmentService.markPaymentPaid(appointmentId)
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

    // Add this endpoint inside your AppointmentController class
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDetailsResponse>> getAppointmentsForDoctor(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean today
    ) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsForDoctor(doctorId, date, status, today)
        );
    }

    // Add this endpoint inside your AppointmentController class
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDetailsResponse>> getAppointmentsForPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean today
    ) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsForPatient(patientId, date, status, today)
        );
    }

    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
        @PathVariable Long appointmentId,
        @RequestBody java.util.Map<String, String> body
    ) {
        String status = body.get("status");
        return ResponseEntity.ok(appointmentService.updateStatus(appointmentId, status));
    }

    /**
     * NEW: Explicit handover transition to ready.
     * POST /api/appointments/{id}/ready
     */
    @PostMapping("/{appointmentId}/ready")
    public ResponseEntity<AppointmentResponse> setAppointmentReady(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(appointmentService.updateStatus(appointmentId, "READY_FOR_DOCTOR"));
    }

    @GetMapping("/doctor/{doctorId}/queue")
    public ResponseEntity<List<AppointmentDetailsResponse>> getDoctorQueue(@PathVariable Long doctorId) {
        List<AppointmentDetailsResponse> inConsultation = appointmentService.getAppointmentsForDoctor(doctorId, null, "IN_CONSULTATION", false);
        List<AppointmentDetailsResponse> ready = appointmentService.getAppointmentsForDoctor(doctorId, null, "READY_FOR_DOCTOR", false);
        
        List<AppointmentDetailsResponse> combined = new java.util.ArrayList<>();
        combined.addAll(inConsultation);
        combined.addAll(ready);
        
        return ResponseEntity.ok(combined);
    }

    /**
     * NEW: Returns all appointments across the system with status READY_FOR_DOCTOR.
     * Used by doctor dashboard to populate the 'Ready for Consultation' section.
     * GET /api/appointments/ready
     */
    @GetMapping("/ready")
    public ResponseEntity<List<AppointmentDetailsResponse>> getReadyAppointments() {
        return ResponseEntity.ok(appointmentService.getReadyAppointments());
    }

    /**
     * NEW: Returns READY_FOR_DOCTOR appointments for a specific doctor.
     * GET /api/appointments/ready/{doctorId}
     */
    @GetMapping("/ready/{doctorId}")
    public ResponseEntity<List<AppointmentDetailsResponse>> getReadyForDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                appointmentService.getAppointmentsForDoctor(doctorId, null, "READY_FOR_DOCTOR", false)
        );
    }
}