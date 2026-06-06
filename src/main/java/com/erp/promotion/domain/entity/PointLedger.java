package com.erp.promotion.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "promotion_point_ledger")
public class PointLedger extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private int delta;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long referenceId;

    protected PointLedger() {}

    public static PointLedger of(Long customerId, int delta, Type type, Long referenceId) {
        PointLedger l = new PointLedger();
        l.customerId = customerId; l.delta = delta; l.type = type; l.referenceId = referenceId;
        return l;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public enum Type { EARN, USE, EXPIRE, REFUND }
}