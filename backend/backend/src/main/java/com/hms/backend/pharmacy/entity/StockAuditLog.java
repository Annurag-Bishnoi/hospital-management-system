package com.hms.backend.pharmacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long auditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(name = "adjustment_quantity", nullable = false)
    private Integer adjustmentQuantity;

    @Column(nullable = false)
    private String reason;

    @Column(name = "adjusted_by", nullable = false)
    private String adjustedBy;

    @Column(nullable = false)
    private LocalDateTime timestamp;

}
