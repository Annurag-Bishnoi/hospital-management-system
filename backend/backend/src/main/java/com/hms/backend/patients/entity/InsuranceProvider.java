package com.hms.backend.patients.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "insurance_providers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuranceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String providerName;

    private Double standardCoveragePercentage; // e.g. 80.0
}
