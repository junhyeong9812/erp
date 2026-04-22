package com.erp.payment.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_payment")
public class Payment extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    private Method method;

    private long amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String pgTransactionId;
    private LocalDateTime paidAt;

    protected Payment() {}

    public static Payment request(Long orderId, Method method, Money amount) {
        Payment p = new Payment();
        p.orderId = orderId;
        p.method = method;
        p.amount = amount.amount().longValueExact();
        p.status = Status.PENDING;
        return p;
    }

    public void complete(String pgTxId) {
        if (status != Status.PENDING) throw new IllegalStateException("결제 가능 상태 아님");
        this.status = Status.COMPLETED;
        this.pgTransactionId = pgTxId;
        this.paidAt = LocalDateTime.now();
        register(new PaymentCompletedEvent(this.id, this.orderId, this.amount, Instant.now()));
    }

    public void fail(String reason) {
        this.status = Status.FAILED;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Money getAmount() { return Money.of(amount); }
    public Status getStatus() { return status; }

    public enum Method { CARD, BANK, VIRTUAL_ACCOUNT }
    public enum Status { PENDING, COMPLETED, FAILED, REFUNDED }
}