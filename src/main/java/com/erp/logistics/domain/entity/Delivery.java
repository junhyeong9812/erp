package com.erp.logistics.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.logistics.domain.event.DeliveryCompletedEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "logistics_delivery")
public class Delivery extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipmentId;
    private String driverId;
    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Delivery() {}

    public static Delivery assign(Long shipmentId, String driverId, String trackingNumber) {
        Delivery d = new Delivery();
        d.shipmentId = shipmentId;
        d.driverId = driverId;
        d.trackingNumber = trackingNumber;
        d.status = Status.ASSIGNED;
        return d;
    }

    public void start() { this.status = Status.IN_TRANSIT; }

    public void deliver() {
        this.status = Status.DELIVERED;
        register(new DeliveryCompletedEvent(this.id, this.shipmentId, Instant.now()));
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getShipmentId() { return shipmentId; }
    public Status getStatus() { return status; }

    public enum Status { ASSIGNED, IN_TRANSIT, DELIVERED, RETURNED }
}