package com.hms.backend.history.repository;

import com.hms.backend.history.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByEntityTypeAndEntityIdOrderByChangedAtDesc(String entityType, Long entityId);
}
