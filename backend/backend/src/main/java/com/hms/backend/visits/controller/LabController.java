package com.hms.backend.visits.controller;

import com.hms.backend.visits.dto.LabResultRequest;
import com.hms.backend.appointments.repository.AppointmentRepository;
import com.hms.backend.appointments.entity.Appointment;
import com.hms.backend.visits.dto.LabTestResponse;
import com.hms.backend.visits.entity.Visit;
import com.hms.backend.visits.entity.VisitLabTest;
import com.hms.backend.visits.repository.VisitLabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lab")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LabController {

    private final VisitLabTestRepository visitLabTestRepository;
    private final AppointmentRepository appointmentRepository;

    /** All lab tests, newest first */
    @GetMapping("/tests")
    public ResponseEntity<List<LabTestResponse>> getAllTests() {
        List<LabTestResponse> result = visitLabTestRepository.findAllByOrderByRecordedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /** Pending tests only */
    @GetMapping("/tests/pending")
    public ResponseEntity<List<LabTestResponse>> getPendingTests() {
        List<LabTestResponse> result = visitLabTestRepository.findByStatus("PENDING")
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /** Tests by status */
    @GetMapping("/tests/status/{status}")
    public ResponseEntity<List<LabTestResponse>> getTestsByStatus(@PathVariable String status) {
        List<LabTestResponse> result = visitLabTestRepository.findByStatus(status.toUpperCase())
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /** Tests for a specific patient */
    @GetMapping("/tests/patient/{patientId}")
    public ResponseEntity<List<LabTestResponse>> getTestsByPatient(@PathVariable Long patientId) {
        List<LabTestResponse> result = visitLabTestRepository.findByPatientId(patientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /** Get single test by id */
    @GetMapping("/tests/{testId}")
    public ResponseEntity<LabTestResponse> getTestById(@PathVariable Long testId) {
        VisitLabTest test = visitLabTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Lab test not found: " + testId));
        return ResponseEntity.ok(toResponse(test));
    }

    /** Lab technician marks test as IN_PROGRESS */
    @PutMapping("/tests/{testId}/start")
    public ResponseEntity<LabTestResponse> startTest(@PathVariable Long testId) {
        VisitLabTest test = visitLabTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Lab test not found: " + testId));
        test.setStatus("IN_PROGRESS");
        return ResponseEntity.ok(toResponse(visitLabTestRepository.save(test)));
    }

    /** Lab technician uploads result and marks COMPLETED */
    @PostMapping("/tests/{testId}/result")
    public ResponseEntity<LabTestResponse> submitResult(
            @PathVariable Long testId,
            @RequestParam("resultValue") String resultValue,
            @RequestParam(value = "referenceRange", required = false) String referenceRange,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        VisitLabTest test = visitLabTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Lab test not found: " + testId));
        
        test.setResultValue(resultValue);
        test.setReferenceRange(referenceRange);
        test.setRemarks(remarks);
        test.setStatus("COMPLETED");

        if (file != null && !file.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
                Path uploadDir = Paths.get("uploads/lab_results");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                Path filePath = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                String fileDownloadUri = "/uploads/lab_results/" + fileName;
                test.setDocumentUrl(fileDownloadUri);
            } catch (IOException ex) {
                throw new RuntimeException("Could not store file. Please try again!", ex);
            }
        }
        
        test = visitLabTestRepository.save(test);

        // Check if all lab tests for this visit are completed
        Visit visit = test.getVisit();
        List<VisitLabTest> allTestsForVisit = visitLabTestRepository.findByVisitVisitId(visit.getVisitId());
        boolean allCompleted = allTestsForVisit.stream()
                .allMatch(t -> "COMPLETED".equalsIgnoreCase(t.getStatus()));
                
        if (allCompleted) {
            Appointment appointment = visit.getAppointment();
            if ("WAITING_FOR_LABS".equalsIgnoreCase(appointment.getStatus())) {
                appointment.setStatus("READY_FOR_DOCTOR");
                appointmentRepository.save(appointment);
            }
        }

        return ResponseEntity.ok(toResponse(test));
    }

    /** Cancel a test */
    @PutMapping("/tests/{testId}/cancel")
    public ResponseEntity<LabTestResponse> cancelTest(@PathVariable Long testId) {
        VisitLabTest test = visitLabTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Lab test not found: " + testId));
        test.setStatus("CANCELLED");
        return ResponseEntity.ok(toResponse(visitLabTestRepository.save(test)));
    }

    @PutMapping("/tests/{testId}/pay")
    public ResponseEntity<LabTestResponse> markPaymentPaid(@PathVariable Long testId) {
        VisitLabTest test = visitLabTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Lab test not found: " + testId));
        test.setPaymentStatus("PAID");
        return ResponseEntity.ok(toResponse(visitLabTestRepository.save(test)));
    }

    /** Mark sample as collected; changes status from PENDING → IN_PROGRESS */
    @PutMapping("/tests/{testId}/sample-collected")
    public ResponseEntity<LabTestResponse> markSampleCollected(@PathVariable Long testId) {
        VisitLabTest test = visitLabTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Lab test not found: " + testId));
        if ("PENDING".equals(test.getStatus())) {
            test.setStatus("IN_PROGRESS");
        }
        return ResponseEntity.ok(toResponse(visitLabTestRepository.save(test)));
    }

    /** Summary counts for dashboard */
    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Long>> getStats() {
        java.util.Map<String, Long> stats = new java.util.LinkedHashMap<>();
        stats.put("total",       visitLabTestRepository.count());
        stats.put("pending",     visitLabTestRepository.countByStatus("PENDING"));
        stats.put("inProgress",  visitLabTestRepository.countByStatus("IN_PROGRESS"));
        stats.put("completed",   visitLabTestRepository.countByStatus("COMPLETED"));
        stats.put("cancelled",   visitLabTestRepository.countByStatus("CANCELLED"));
        return ResponseEntity.ok(stats);
    }

    /* ── Mapper ── */
    private LabTestResponse toResponse(VisitLabTest t) {
        Visit visit = t.getVisit();
        return LabTestResponse.builder()
                .testId(t.getTestId())
                .visitId(visit != null ? visit.getVisitId() : null)
                .patientId(t.getPatientId())
                .patientName(visit != null && visit.getPatient() != null ? visit.getPatient().getFullName() : "—")
                .doctorId(visit != null && visit.getDoctor() != null ? visit.getDoctor().getDoctorId() : null)
                .doctorName(visit != null && visit.getDoctor() != null ? visit.getDoctor().getFullName() : "—")
                .appointmentId(visit != null && visit.getAppointment() != null ? visit.getAppointment().getAppointmentId() : null)
                .testCode(t.getTestCode())
                .testName(t.getTestName())
                .status(t.getStatus())
                .resultValue(t.getResultValue())
                .remarks(t.getRemarks())
                .referenceRange(t.getReferenceRange())
                .paymentStatus(t.getPaymentStatus())
                .recordedAt(t.getRecordedAt())
                .documentUrl(t.getDocumentUrl())
                .build();
    }
}
