package com.hms.backend.doctors.controller;

import com.hms.backend.doctors.dto.DoctorRegisterRequest;
import com.hms.backend.doctors.dto.DoctorStatusRequest;
import com.hms.backend.doctors.dto.DoctorUpdateRequest;
import com.hms.backend.doctors.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(
            @RequestBody DoctorRegisterRequest request
    ) {
        return ResponseEntity.ok(
                doctorService.registerDoctor(request)
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.ok(
                doctorService.getAllDoctors()
        );
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<?> getDoctorById(
            @PathVariable Long doctorId
    ) {
        return ResponseEntity.ok(
                doctorService.getDoctorById(doctorId)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDoctorByUserId(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                doctorService.getDoctorByUserId(userId)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDoctors(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(
                doctorService.searchDoctors(keyword)
        );
    }

    @PutMapping("/{doctorId}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable Long doctorId,
            @RequestBody DoctorUpdateRequest request
    ) {
        return ResponseEntity.ok(
                doctorService.updateDoctor(
                        doctorId,
                        request
                )
        );
    }

    @PatchMapping("/{doctorId}/status")
    public ResponseEntity<?> updateDoctorStatus(
            @PathVariable Long doctorId,
            @RequestBody DoctorStatusRequest request
    ) {
        return ResponseEntity.ok(
                doctorService.updateDoctorStatus(
                        doctorId,
                        request
                )
        );
    }

    @PostMapping("/{doctorId}/reset-credentials")
    public ResponseEntity<?> resetCredentials(
            @PathVariable Long doctorId
    ) {
        try {
            return ResponseEntity.ok(
                    doctorService.resetCredentials(doctorId)
            );
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}