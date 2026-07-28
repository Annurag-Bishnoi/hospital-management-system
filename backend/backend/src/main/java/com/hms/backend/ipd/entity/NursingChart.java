package com.hms.backend.ipd.entity;

import com.hms.backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "nursing_charts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NursingChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_id", nullable = false)
    private Admission admission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", nullable = false)
    private User nurse;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    private Double temperature;
    private String bloodPressure;
    private Integer heartRate;
    private Integer spo2;

    @Column(columnDefinition = "TEXT")
    private String nursingNotes;
}
