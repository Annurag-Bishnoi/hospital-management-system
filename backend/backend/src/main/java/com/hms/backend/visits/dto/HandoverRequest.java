package com.hms.backend.visits.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HandoverRequest {
    private Long appointmentId;
}
