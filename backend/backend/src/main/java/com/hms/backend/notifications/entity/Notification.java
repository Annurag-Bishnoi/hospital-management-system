package com.hms.backend.notifications.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // The recipient

    @Column(nullable = false, length = 255)
    private String message;

    @Column(nullable = false, length = 50)
    private String type; // e.g. LAB_RESULT, NEW_APPOINTMENT

    @Column(nullable = false)
    private Boolean readStatus;

    @Column
    private Long referenceId; // e.g. visitId or appointmentId

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (readStatus == null) {
            readStatus = false;
        }
    }
}
