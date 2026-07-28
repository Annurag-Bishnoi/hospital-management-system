package com.hms.backend.ipd.controller;

import com.hms.backend.ipd.dto.IpdDto.*;
import com.hms.backend.ipd.service.IpdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ipd")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class IpdController {

    private final IpdService ipdService;

    @GetMapping("/wards")
    public ResponseEntity<List<WardDto>> getAllWards() {
        return ResponseEntity.ok(ipdService.getAllWards());
    }

    @GetMapping("/beds")
    public ResponseEntity<List<BedDto>> getAllBeds() {
        return ResponseEntity.ok(ipdService.getAllBeds());
    }

    @GetMapping("/wards/{wardId}/beds")
    public ResponseEntity<List<BedDto>> getBedsByWard(@PathVariable Long wardId) {
        return ResponseEntity.ok(ipdService.getBedsByWard(wardId));
    }

    @PostMapping("/admissions")
    public ResponseEntity<AdmissionResponse> requestAdmission(@RequestBody AdmissionRequest request, Authentication authentication) {
        return ResponseEntity.ok(ipdService.requestAdmission(request, authentication.getName()));
    }

    @PutMapping("/admissions/{id}/assign-bed")
    public ResponseEntity<AdmissionResponse> assignBed(@PathVariable Long id, @RequestBody AssignBedRequest request, Authentication authentication) {
        return ResponseEntity.ok(ipdService.assignBed(id, request, authentication.getName()));
    }

    @PutMapping("/admissions/{id}/discharge")
    public ResponseEntity<AdmissionResponse> dischargePatient(@PathVariable Long id, @RequestBody DischargeRequest request, Authentication authentication) {
        return ResponseEntity.ok(ipdService.dischargePatient(id, request, authentication.getName()));
    }

    @GetMapping("/admissions")
    public ResponseEntity<List<AdmissionResponse>> getAdmissionsByStatus(@RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(ipdService.getAdmissionsByStatus(status));
        }
        // Fallback to active admissions if needed, but returning admitted for now
        return ResponseEntity.ok(ipdService.getAdmissionsByStatus("ADMITTED"));
    }
    
    @GetMapping("/admissions/pending")
    public ResponseEntity<List<AdmissionResponse>> getPendingAdmissions() {
        return ResponseEntity.ok(ipdService.getAdmissionsByStatus("REQUESTED"));
    }

    @GetMapping("/admissions/doctor")
    public ResponseEntity<List<AdmissionResponse>> getDoctorAdmissions(Authentication authentication) {
        return ResponseEntity.ok(ipdService.getAdmissionsByDoctor(authentication.getName()));
    }

    @PostMapping("/admissions/{id}/rounds")
    public ResponseEntity<DailyRoundResponse> addDailyRound(@PathVariable Long id, @RequestBody DailyRoundRequest request, Authentication authentication) {
        return ResponseEntity.ok(ipdService.addDailyRound(id, request, authentication.getName()));
    }

    @GetMapping("/admissions/{id}/rounds")
    public ResponseEntity<List<DailyRoundResponse>> getDailyRounds(@PathVariable Long id) {
        return ResponseEntity.ok(ipdService.getDailyRounds(id));
    }

    @PostMapping("/admissions/{id}/charts")
    public ResponseEntity<NursingChartResponse> addNursingChart(@PathVariable Long id, @RequestBody NursingChartRequest request, Authentication authentication) {
        return ResponseEntity.ok(ipdService.addNursingChart(id, request, authentication.getName()));
    }

    @GetMapping("/admissions/{id}/charts")
    public ResponseEntity<List<NursingChartResponse>> getNursingCharts(@PathVariable Long id) {
        return ResponseEntity.ok(ipdService.getNursingCharts(id));
    }
}
