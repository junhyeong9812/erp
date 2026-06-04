package com.erp.crm.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.crm.domain.event.CustomerGradeChangedEvent;
import com.erp.crm.domain.event.CustomerRegisteredEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "crm_customer")
public class Customer extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String customerCode;
    private String name;
    private String contact;
    private Long assignedSalesEmployeeId;

    private long creditLimit;
    private long totalPurchase;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    protected Customer() {}

    public static Customer register(String customerCode, String name, String contact,
                                    Long assignedSalesEmployeeId, Money creditLimit) {
        Customer c = new Customer();
        c.customerCode = customerCode;
        c.name = name;
        c.contact = contact;
        c.assignedSalesEmployeeId = assignedSalesEmployeeId;
        c.creditLimit = creditLimit.amount().longValueExact();
        c.grade = Grade.NORMAL;
        c.register(new CustomerRegisteredEvent(null, customerCode, name, Instant.now()));
        return c;
    }

    public void recordPurchase(Money amount) {
        this.totalPurchase += amount.amount().longValueExact();
        Grade newGrade = computeGrade();
        if (newGrade != this.grade) {
            Grade old = this.grade;
            this.grade = newGrade;
            register(new CustomerGradeChangedEvent(this.id, old.name(), newGrade.name(), Instant.now()));
        }
    }

    private Grade computeGrade() {
        if (totalPurchase >= 10_000_000) return Grade.VIP;
        if (totalPurchase >= 5_000_000) return Grade.GOLD;
        if (totalPurchase >= 1_000_000) return Grade.SILVER;
        return Grade.NORMAL;
    }

    public boolean canCharge(Money amount) {
        return amount.amount().longValueExact() <= creditLimit;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getCustomerCode() { return customerCode; }
    public String getName() { return name; }
    public Grade getGrade() { return grade; }
    public Money getCreditLimit() { return Money.of(creditLimit); }
    public Money getTotalPurchase() { return Money.of(totalPurchase); }

    public enum Grade { NORMAL, SILVER, GOLD, VIP }
}