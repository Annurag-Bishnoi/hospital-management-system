package com.hms.backend.visits.repository;



import com.hms.backend.visits.entity.VisitVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitVitalRepository extends JpaRepository<VisitVital, Long> {
    List<VisitVital> findByVisitVisitId(Long visitId);
    List<VisitVital> findByPatientId(Long patientId);
    boolean existsByVisit_VisitId(Long visitId);
    boolean existsByVisit_Appointment_AppointmentId(Long appointmentId);
}
