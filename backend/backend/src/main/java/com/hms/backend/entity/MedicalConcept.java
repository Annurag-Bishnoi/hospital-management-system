package com.hms.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medical_concepts")
public class MedicalConcept {

    @Id
    private String cielId;

    private String conceptName;
    private String conceptClass;

    // 1. Default constructor required by JPA
    public MedicalConcept() {
    }

    // 2. Custom constructor used in our DatabaseSeeder
    public MedicalConcept(String cielId, String conceptName, String conceptClass) {
        this.cielId = cielId;
        this.conceptName = conceptName;
        this.conceptClass = conceptClass;
    }

    // 3. Getters
    public String getCielId() {
        return cielId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public String getConceptClass() {
        return conceptClass;
    }

    // 4. Setters
    public void setCielId(String cielId) {
        this.cielId = cielId;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public void setConceptClass(String conceptClass) {
        this.conceptClass = conceptClass;
    }
}
