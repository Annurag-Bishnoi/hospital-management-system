package com.hms.backend.patients.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientAllergyRequest {
    private String allergyName;
    private String severity;
    private String notes;
}
