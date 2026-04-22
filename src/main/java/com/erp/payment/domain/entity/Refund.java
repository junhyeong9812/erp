package com.erp.payment.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.payment.domain.event.RefundCompletedEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "payment_refund")
public class Refund extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;
    private Long orderId;
    private long amount;
    private String reason;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Refund() {}

    public static Refund request(Long paymentId, Long orderId, Money amount, String reason) {
        Refund r = new Refund();
        r.paymentId = paymentId;
        r.orderId = orderId;
        r.amount = amount.amount().longValueExact();
        r.reason = reason;
        r.status = Status.PENDING;
        return r;
    }

    public void complete() {
        this.status = Status.COMPLETED;
        register(new RefundCompletedEvent(this.id, this.paymentId, this.orderId, this.amount, Instant.now()));
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Long getPaymentId() { return paymentId; }
    public Long getOrderId() { return orderId; }
    public Money getAmount() { return Money.of(amount); }
    public Status getStatus() { return status; }

    public enum Status { PENDING, COMPLETED, FAILED }
}