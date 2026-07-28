package com.hms.backend.pharmacy.repository;

import com.hms.backend.pharmacy.entity.InventoryItem;
import com.hms.backend.pharmacy.entity.MedicineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, Long> {
    List<MedicineBatch> findByInventoryItemOrderByExpiryDateAsc(InventoryItem item);
    List<MedicineBatch> findByExpiryDateBefore(LocalDate date);
}
