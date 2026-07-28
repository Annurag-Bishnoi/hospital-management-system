package com.hms.backend.ipd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., General Ward, ICU, VIP Suite

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private BigDecimal dailyCharge;
}
