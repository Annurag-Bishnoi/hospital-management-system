package com.hms.backend.ipd.service;

import com.hms.backend.ipd.dto.IpdDto.*;
import java.util.List;

public interface IpdService {
    List<WardDto> getAllWards();
    List<BedDto> getAllBeds();
    List<BedDto> getBedsByWard(Long wardId);
    
    AdmissionResponse requestAdmission(AdmissionRequest request, String currentUser);
    AdmissionResponse assignBed(Long admissionId, AssignBedRequest request, String currentUser);
    AdmissionResponse dischargePatient(Long admissionId, DischargeRequest request, String currentUser);
    AdmissionResponse cancelAdmission(Long admissionId, String currentUser);
    
    List<AdmissionResponse> getAdmissionsByStatus(String status);
    List<AdmissionResponse> getAdmissionsByDoctor(String currentUser);
    List<AdmissionResponse> getAdmissionsByPatient(Long patientId);
    
    DailyRoundResponse addDailyRound(Long admissionId, DailyRoundRequest request, String currentUser);
    List<DailyRoundResponse> getDailyRounds(Long admissionId);
    
    NursingChartResponse addNursingChart(Long admissionId, NursingChartRequest request, String currentUser);
    List<NursingChartResponse> getNursingCharts(Long admissionId);
}
