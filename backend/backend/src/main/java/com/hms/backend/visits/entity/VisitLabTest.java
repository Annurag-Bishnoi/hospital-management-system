package com.hms.backend.visits.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_lab_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitLabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    // The CIEL concept code for the test (e.g., Complete Blood Count)
    @Column(name = "test_code", nullable = false, length = 50)
    private String testCode;

    // The human-readable name of the test
    @Column(name = "test_name", nullable = false, length = 255)
    private String testName;

    // Status: e.g., "PENDING", "COMPLETED", "CANCELLED"
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    // The result value, filled later by lab technician
    @Column(name = "result_value", columnDefinition = "TEXT")
    private String resultValue;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "reference_range", length = 255)
    private String referenceRange;

    // e.g., "PENDING", "PAID"
    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @Column(name = "document_url", length = 1000)
    private String documentUrl;

    @PrePersist
    public void prePersist() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = "PENDING";
        }
        if (paymentStatus == null) {
            paymentStatus = "PENDING";
        }
    }
}
