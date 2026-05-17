package com.erp.production.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "production_plan")
public class ProductionPlan extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private LocalDate targetDate;
    private int plannedQuantity;

    protected ProductionPlan() {}

    public static ProductionPlan plan(Long productId, LocalDate targetDate, int quantity) {
        ProductionPlan p = new ProductionPlan();
        p.productId = productId;
        p.targetDate = targetDate;
        p.plannedQuantity = quantity;
        return p;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public int getPlannedQuantity() { return plannedQuantity; }
}