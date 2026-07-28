package com.hms.backend.ipd.repository;

import com.hms.backend.ipd.entity.NursingChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NursingChartRepository extends JpaRepository<NursingChart, Long> {
    List<NursingChart> findByAdmissionIdOrderByRecordedAtDesc(Long admissionId);
}
