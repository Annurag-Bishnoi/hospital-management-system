package com.hms.backend.visits.service;

import com.hms.backend.visits.dto.EncounterRequest;
import com.hms.backend.visits.dto.HandoverRequest;
import com.hms.backend.visits.dto.VisitRequest;
import com.hms.backend.visits.dto.VisitResponse;
import java.util.List;

public interface VisitService {
    VisitResponse createVisit(VisitRequest request);
    VisitResponse updateVisit(Long visitId, VisitRequest request);
    VisitResponse getVisitById(Long visitId);
    List<VisitResponse> getAllVisits();
    List<VisitResponse> getVisitsByPatientId(Long patientId);
    List<VisitResponse> getVisitsByDoctorId(Long doctorId);
    List<VisitResponse> getVisitsByAppointmentId(Long appointmentId);
    void deleteVisit(Long visitId);
    VisitResponse handover(HandoverRequest request);
    VisitResponse completeEncounter(Long visitId, EncounterRequest request);
    VisitResponse startConsultation(Long visitId);
    VisitResponse startConsultationByAppointment(Long appointmentId);
}