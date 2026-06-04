package com.erp.crm.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "crm_claim")
public class Claim extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String resolution;

    protected Claim() {}

    public static Claim open(Long customerId, String description) {
        Claim c = new Claim();
        c.customerId = customerId; c.description = description;
        c.status = Status.OPEN;
        return c;
    }

    public void resolve(String resolution) {
        this.status = Status.RESOLVED;
        this.resolution = resolution;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Status getStatus() { return status; }

    public enum Status { OPEN, IN_PROGRESS, RESOLVED, CLOSED }
}