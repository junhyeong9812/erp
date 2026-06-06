package com.erp.promotion.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.common.domain.Money;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "promotion_coupon")
public class Coupon extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;
    private Long customerId;
    private int discountRate;          // 1~100 %
    private long discountAmount;       // 정액 할인이면 사용
    private LocalDate expireOn;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Coupon() {}

    public static Coupon issue(String code, Long customerId, int rate, Money amount, LocalDate expireOn) {
        Coupon c = new Coupon();
        c.code = code; c.customerId = customerId;
        c.discountRate = rate; c.discountAmount = amount.amount().longValueExact();
        c.expireOn = expireOn; c.status = Status.ISSUED;
        return c;
    }

    public Money apply(Money subtotal) {
        if (status != Status.ISSUED) throw new IllegalStateException("사용 불가 쿠폰");
        long discount = discountRate > 0
                ? subtotal.amount().longValueExact() * discountRate / 100
                : discountAmount;
        this.status = Status.USED;
        return Money.of(Math.max(0, subtotal.amount().longValueExact() - discount));
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getCode() { return code; }
    public Status getStatus() { return status; }

    public enum Status { ISSUED, USED, EXPIRED }
}