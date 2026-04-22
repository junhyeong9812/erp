package com.erp.payment.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import jakarta.persistence.*;

@Entity
@Table(name = "payment_order")
public class Order extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    private Long customerId;

    private long totalAmount;
    private long paidAmount;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Order() {}

    public static Order create(String orderNumber, Long customerId, Money total) {
        Order o = new Order();
        o.orderNumber = orderNumber;
        o.customerId = customerId;
        o.totalAmount = total.amount().longValueExact();
        o.paidAmount = 0;
        o.status = Status.PENDING;
        return o;
    }

    public void applyPayment(Money amount) {
        long add = amount.amount().longValueExact();
        if (paidAmount + add > totalAmount) {
            throw new IllegalStateException("총액 초과 결제");
        }
        this.paidAmount += add;
        if (paidAmount == totalAmount) {
            this.status = Status.PAID;
        }
    }

    public void refund(Money amount) {
        long sub = amount.amount().longValueExact();
        if (sub > paidAmount) throw new IllegalStateException("환불 금액이 결제액보다 큼");
        this.paidAmount -= sub;
        if (paidAmount == 0) this.status = Status.CANCELLED;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public Long getCustomerId() { return customerId; }
    public Money getTotalAmount() { return Money.of(totalAmount); }
    public Money getPaidAmount() { return Money.of(paidAmount); }
    public Money getRemainingAmount() { return Money.of(totalAmount - paidAmount); }
    public Status getStatus() { return status; }

    public enum Status { PENDING, PAID, CANCELLED }
}