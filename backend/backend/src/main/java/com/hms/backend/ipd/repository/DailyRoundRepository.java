package com.hms.backend.ipd.repository;

import com.hms.backend.ipd.entity.DailyRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyRoundRepository extends JpaRepository<DailyRound, Long> {
    List<DailyRound> findByAdmissionIdOrderByRoundDateDesc(Long admissionId);
}
