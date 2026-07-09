package com.hms.backend.visits.service;

import com.hms.backend.visits.dto.SimpleVitalsRequest;
import com.hms.backend.visits.dto.SimpleVitalsResponse;
import com.hms.backend.visits.dto.VisitVitalRequest;
import com.hms.backend.visits.dto.VisitVitalResponse;
import com.hms.backend.entity.MedicalConcept;
import java.util.List;

public interface VisitVitalService {
    // NEW: single structured vitals + auto READY_FOR_DOCTOR transition
    SimpleVitalsResponse recordSimpleVitals(Long appointmentId, SimpleVitalsRequest request);

    // CIEL-based batch vitals (kept for backward compat)
    List<VisitVitalResponse> recordVitals(Long visitId, List<VisitVitalRequest> requests);

    List<VisitVitalResponse> getVitalsByVisitId(Long visitId);

    List<MedicalConcept> searchVitalConcepts(String searchTerm);
}