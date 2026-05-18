package com.erp.production.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.production.domain.event.ProductionCompletedEvent;
import com.erp.production.domain.event.WorkOrderIssuedEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "production_work_order")
public class WorkOrder extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private int plannedQuantity;
    private int producedQuantity;
    private int defectiveQuantity;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected WorkOrder() {}

    public static WorkOrder issue(Long productId, int plannedQuantity) {
        WorkOrder w = new WorkOrder();
        w.productId = productId;
        w.plannedQuantity = plannedQuantity;
        w.status = Status.PLANNED;
        w.register(new WorkOrderIssuedEvent(null, productId, plannedQuantity, Instant.now()));
        return w;
    }

    public void recordProduction(int produced, int defective) {
        this.producedQuantity += produced;
        this.defectiveQuantity += defective;
        if (producedQuantity >= plannedQuantity) {
            complete();
        } else {
            this.status = Status.IN_PROGRESS;
        }
    }

    private void complete() {
        this.status = Status.COMPLETED;
        register(new ProductionCompletedEvent(this.id, this.productId, this.producedQuantity, Instant.now()));
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public int getPlannedQuantity() { return plannedQuantity; }
    public int getProducedQuantity() { return producedQuantity; }
    public Status getStatus() { return status; }

    public enum Status { PLANNED, IN_PROGRESS, COMPLETED, CANCELLED }
}