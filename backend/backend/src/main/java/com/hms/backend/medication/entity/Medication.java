package com.hms.backend.medication.entity;


import com.hms.backend.prescription.entity.Prescription;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medications")
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long medicationId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prescription_id", nullable = false, updatable = false)
    private Prescription prescription;

    @Column(nullable = false, length = 100)
    private String medicineName;

    @Column(name = "medication_code", length = 50)
    private String medicationCode;

    @Column(nullable = false, length = 50)
    private String dosage;

    @Column(nullable = false, length = 50)
    private String frequency;

    @Column(nullable = false, length = 50)
    private String duration;

    @Column(name = "quantity", length = 50)
    private String quantity;

    @Column(length = 255)
    private String instructions;
}