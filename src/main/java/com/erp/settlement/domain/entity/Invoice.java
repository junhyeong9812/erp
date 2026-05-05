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

    @Enumerated(EnumType.STRING)
    private Status status;

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
        i.status = Status.OUTSTANDING;
        i.customerId = customerId;
        i.amount = amount.amount().longValueExact();
        i.taxAmount = tax.amount().longValueExact();
        i.issuedAt = LocalDateTime.now();
        i.periodId = periodId;
        return i;
    }

    public void markPaid() { this.status = Status.PAID; }
    public void cancel()   { this.status = Status.CANCELLED; }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public Type getType() { return type; }
    public Status getStatus() { return status; }
    public Long getCustomerId() { return customerId; }
    public Money getAmount() { return Money.of(amount); }
    public Money getTaxAmount() { return Money.of(taxAmount); }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public Long getPeriodId() { return periodId; }

    public enum Type { SALES, PURCHASE }
    public enum Status { OUTSTANDING, PAID, CANCELLED }
}
