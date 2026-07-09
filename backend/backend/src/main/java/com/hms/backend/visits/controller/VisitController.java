package com.hms.backend.visits.controller;

import com.hms.backend.visits.dto.EncounterRequest;
import com.hms.backend.visits.dto.HandoverRequest;
import com.hms.backend.visits.dto.VisitRequest;
import com.hms.backend.visits.dto.VisitResponse;
import com.hms.backend.visits.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<VisitResponse> createVisit(@Valid @RequestBody VisitRequest request) {
        return new ResponseEntity<>(visitService.createVisit(request), HttpStatus.CREATED);
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<VisitResponse> updateVisit(@PathVariable Long visitId, @Valid @RequestBody VisitRequest request) {
        return ResponseEntity.ok(visitService.updateVisit(visitId, request));
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<VisitResponse> getVisitById(@PathVariable Long visitId) {
        return ResponseEntity.ok(visitService.getVisitById(visitId));
    }

    @GetMapping
    public ResponseEntity<List<VisitResponse>> getAllVisits() {
        return ResponseEntity.ok(visitService.getAllVisits());
    }

    /**
     * NEW: HIPAA-compliant masked endpoint for receptionists.
     * Hides Diagnosis and Clinical Notes.
     */
    @GetMapping("/receptionist")
    public ResponseEntity<List<VisitResponse>> getVisitsForReceptionist() {
        List<VisitResponse> visits = visitService.getAllVisits();
        visits.forEach(v -> {
            v.setDiagnosis(null);
            v.setNotes(null);
        });
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VisitResponse>> getVisitsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(visitService.getVisitsByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<VisitResponse>> getVisitsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(visitService.getVisitsByDoctorId(doctorId));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<VisitResponse>> getVisitsByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(visitService.getVisitsByAppointmentId(appointmentId));
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long visitId) {
        visitService.deleteVisit(visitId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/handover")
    public ResponseEntity<VisitResponse> handover(@RequestBody HandoverRequest request) {
        return ResponseEntity.ok(visitService.handover(request));
    }

    @PutMapping("/{visitId}/encounter")
    public ResponseEntity<VisitResponse> completeEncounter(
            @PathVariable Long visitId,
            @RequestBody EncounterRequest request) {
        return ResponseEntity.ok(visitService.completeEncounter(visitId, request));
    }

    /**
     * Doctor clicks "Start Consultation".
     * Transitions: appointment READY_FOR_DOCTOR → IN_CONSULTATION
     * Locks the visit so only this doctor can modify it.
     */
    @PutMapping("/{visitId}/start")
    public ResponseEntity<VisitResponse> startConsultation(@PathVariable Long visitId) {
        return ResponseEntity.ok(visitService.startConsultation(visitId));
    }

    /**
     * NEW: POST variant of start consultation.
     * POST /api/visits/start
     */
    @PostMapping("/start")
    public ResponseEntity<VisitResponse> startConsultationByPost(@RequestBody HandoverRequest request) {
        return ResponseEntity.ok(visitService.startConsultationByAppointment(request.getAppointmentId()));
    }
}