package com.erp.logistics.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "logistics_picking_list")
public class PickingList extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipmentId;
    private Long productId;
    private int quantity;
    private String location;

    protected PickingList() {}

    public static PickingList of(Long shipmentId, Long productId, int quantity, String location) {
        PickingList p = new PickingList();
        p.shipmentId = shipmentId;
        p.productId = productId;
        p.quantity = quantity;
        p.location = location;
        return p;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
}