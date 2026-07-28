package com.hms.backend.pharmacy.repository;

import com.hms.backend.pharmacy.entity.StockAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAuditLogRepository extends JpaRepository<StockAuditLog, Long> {
}
