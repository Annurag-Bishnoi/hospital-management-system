package com.hms.backend.prescription.dto;

import com.hms.backend.medication.dto.MedicationDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponse {
    private Long prescriptionId;
    private Long appointmentId;
    private Long patientId;
    private Long doctorId;
    private String patientName;
    private String doctorName;
    private String diagnosis;
    private String notes;
    private List<MedicationDto> medications;
    private LocalDateTime createdAt;
}
