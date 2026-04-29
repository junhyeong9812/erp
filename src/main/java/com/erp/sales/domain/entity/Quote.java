package com.erp.sales.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.sales.domain.event.QuoteExpiredEvent;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "sales_quote")
public class Quote extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private long totalAmount;
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Quote() {}

    public static Quote issue(Long customerId, Money total, LocalDate validUntil) {
        Quote q = new Quote();
        q.customerId = customerId;
        q.totalAmount = total.amount().longValueExact();
        q.validUntil = validUntil;
        q.status = Status.ACTIVE;
        return q;
    }

    public void accept() {
        if (status != Status.ACTIVE) throw new IllegalStateException("수락 불가 상태");
        this.status = Status.ACCEPTED;
    }

    public void expire() {
        if (status != Status.ACTIVE) return;
        this.status = Status.EXPIRED;
        register(new QuoteExpiredEvent(this.id, this.customerId, Instant.now()));
    }

    public boolean isExpired(LocalDate today) {
        return today.isAfter(validUntil);
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Money getTotalAmount() { return Money.of(totalAmount); }
    public LocalDate getValidUntil() { return validUntil; }
    public Status getStatus() { return status; }

    public enum Status { ACTIVE, ACCEPTED, EXPIRED, REJECTED }
}