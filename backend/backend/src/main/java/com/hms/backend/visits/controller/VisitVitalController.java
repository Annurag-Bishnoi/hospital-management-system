package com.hms.backend.visits.controller;

import com.hms.backend.entity.MedicalConcept;
import com.hms.backend.visits.dto.SimpleVitalsRequest;
import com.hms.backend.visits.dto.SimpleVitalsResponse;
import com.hms.backend.visits.dto.VisitVitalRequest;
import com.hms.backend.visits.dto.VisitVitalResponse;
import com.hms.backend.visits.service.VisitVitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VisitVitalController {

    private final VisitVitalService visitVitalService;

    /**
     * NEW ENDPOINT — Fixes HttpMessageNotReadableException.
     * Accepts a SINGLE structured vitals object (not a List).
     * Auto-creates a Visit if none exists, then transitions
     * the Appointment status to READY_FOR_DOCTOR.
     *
     * POST /api/vitals?appointmentId={id}
     */
    @RequestMapping(value = "/api/vitals", method = RequestMethod.POST)
    public ResponseEntity<SimpleVitalsResponse> recordSimpleVitals(
            @RequestParam Long appointmentId,
            @Valid @RequestBody SimpleVitalsRequest request) {
        return new ResponseEntity<>(visitVitalService.recordSimpleVitals(appointmentId, request), HttpStatus.CREATED);
    }

    // Existing CIEL-based batch vitals (kept for backward compat)
    @RequestMapping(value = "/api/visits/{visitId}/vitals", method = RequestMethod.POST)
    public ResponseEntity<List<VisitVitalResponse>> recordVitals(
            @PathVariable Long visitId,
            @Valid @RequestBody List<VisitVitalRequest> requests) {
        List<VisitVitalResponse> savedVitals = visitVitalService.recordVitals(visitId, requests);
        return new ResponseEntity<>(savedVitals, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/api/visits/{visitId}/vitals", method = RequestMethod.GET)
    public ResponseEntity<List<VisitVitalResponse>> getVitalsByVisitId(@PathVariable Long visitId) {
        return ResponseEntity.ok(visitVitalService.getVitalsByVisitId(visitId));
    }

    @RequestMapping(value = "/api/vitals/appointment/{appointmentId}", method = RequestMethod.GET)
    public ResponseEntity<List<VisitVitalResponse>> getVitalsByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(visitVitalService.getVitalsByAppointmentId(appointmentId));
    }

    @RequestMapping(value = "/api/visits/vitals/dictionary/search", method = RequestMethod.GET)
    public ResponseEntity<List<MedicalConcept>> searchVitalConcepts(@RequestParam String term) {
        if (term == null || term.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(visitVitalService.searchVitalConcepts(term));
    }
}