package com.erp.promotion.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.promotion.domain.event.PointEarnedEvent;
import com.erp.promotion.domain.event.PointExpiredEvent;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "promotion_point")
public class Point extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private int amount;           // 남은 포인트
    private LocalDate expireOn;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Point() {}

    public static Point earn(Long customerId, int amount, LocalDate expireOn) {
        Point p = new Point();
        p.customerId = customerId;
        p.amount = amount;
        p.expireOn = expireOn;
        p.status = Status.ACTIVE;
        p.register(new PointEarnedEvent(null, customerId, amount, Instant.now()));
        return p;
    }

    public int consume(int request) {
        if (status != Status.ACTIVE) return 0;
        int used = Math.min(amount, request);
        this.amount -= used;
        if (amount == 0) this.status = Status.USED;
        return used;
    }

    public void expire() {
        if (status != Status.ACTIVE) return;
        this.status = Status.EXPIRED;
        register(new PointExpiredEvent(this.id, this.customerId, this.amount, Instant.now()));
        this.amount = 0;
    }

    public boolean isExpirable(LocalDate today) {
        return status == Status.ACTIVE && !today.isBefore(expireOn);
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public int getAmount() { return amount; }
    public Status getStatus() { return status; }

    public enum Status { ACTIVE, USED, EXPIRED }
}