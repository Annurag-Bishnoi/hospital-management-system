package com.hms.backend.visits.entity;



import com.hms.backend.visits.entity.Visit; // Adjust import to your Visit entity
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visit_vitals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vitalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    // Redundant but highly recommended for fast querying without joining the Visit table
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    // STANDARDIZATION: Always use the cielId as your primary reference
    @Column(name = "ciel_id", nullable = false)
    private Long cielId;

    // Stored as String to handle numeric values (98.6) or text values (e.g., "Positive")
    @Column(name = "vital_value", nullable = false, length = 100)
    private String vitalValue;

    @Column(name = "unit", length = 50)
    private String unit; // e.g., "mmHg", "kg", "°C", "bpm"

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
