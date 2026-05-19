package com.erp.hr.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.hr.domain.event.EmployeeHiredEvent;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "hr_employee")
public class Employee extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String employeeNumber;

    private String name;
    private Long departmentId;
    private LocalDate hiredAt;
    private LocalDate terminatedAt;

    private long baseSalary;
    private int remainingLeaveDays;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Employee() {}

    public static Employee hire(String employeeNumber, String name, Long departmentId,
                                LocalDate hiredAt, Money baseSalary) {
        Employee e = new Employee();
        e.employeeNumber = employeeNumber;
        e.name = name;
        e.departmentId = departmentId;
        e.hiredAt = hiredAt;
        e.baseSalary = baseSalary.amount().longValueExact();
        e.remainingLeaveDays = 15;
        e.status = Status.ACTIVE;
        e.register(new EmployeeHiredEvent(null, employeeNumber, name, hiredAt, Instant.now()));
        return e;
    }

    public void transferTo(Long newDepartmentId) {
        this.departmentId = newDepartmentId;
    }

    public void terminate(LocalDate date) {
        this.status = Status.TERMINATED;
        this.terminatedAt = date;
    }

    public void consumeLeave(int days) {
        if (remainingLeaveDays < days) throw new IllegalStateException("잔여 연차 부족");
        this.remainingLeaveDays -= days;
    }

    public Money getBaseSalary() { return Money.of(baseSalary); }
    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public String getEmployeeNumber() { return employeeNumber; }
    public String getName() { return name; }
    public Long getDepartmentId() { return departmentId; }
    public int getRemainingLeaveDays() { return remainingLeaveDays; }
    public Status getStatus() { return status; }

    public enum Status { ACTIVE, ON_LEAVE, TERMINATED }
}