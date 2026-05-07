package com.erp.logistics.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.logistics.domain.event.ShipmentDispatchedEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "logistics_shipment")
public class Shipment extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long warehouseId;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Shipment() {}

    public static Shipment instruct(Long orderId, Long warehouseId) {
        Shipment s = new Shipment();
        s.orderId = orderId;
        s.warehouseId = warehouseId;
        s.status = Status.PREPARING;
        return s;
    }

    public void dispatch() {
        if (status != Status.PREPARING) throw new IllegalStateException();
        this.status = Status.DISPATCHED;
        register(new ShipmentDispatchedEvent(this.id, this.orderId, Instant.now()));
    }

    public void complete() {
        this.status = Status.COMPLETED;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Status getStatus() { return status; }

    public enum Status { PREPARING, DISPATCHED, COMPLETED, CANCELLED }
}