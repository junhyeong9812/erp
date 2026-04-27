package com.erp.settlement.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.common.domain.Money;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_invoice")
public class Invoice extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long customerId;
    private long amount;
    private long taxAmount;
    private LocalDateTime issuedAt;
    private Long periodId;

    protected Invoice() {}

    public static Invoice issue(String number, Type type, Long customerId,
                                Money amount, Money tax, Long periodId) {
        Invoice i = new Invoice();
        i.invoiceNumber = number;
        i.type = type;
        i.customerId = customerId;
        i.amount = amount.amount().longValueExact();
        i.taxAmount = tax.amount().longValueExact();
        i.issuedAt = LocalDateTime.now();
        i.periodId = periodId;
        return i;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public enum Type { SALES, PURCHASE }
}