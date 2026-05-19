package com.erp.hr.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.hr.domain.event.PayrollCalculatedEvent;
import com.erp.hr.domain.vo.AllowanceBreakdown;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.YearMonth;

@Entity
@Table(name = "hr_payroll")
public class Payroll extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private String period;

    private long baseSalary;

    @Embedded
    private AllowanceBreakdown allowance;  // long allowance → 항목별 스냅샷으로 교체

    private long deduction;
    private long netSalary;

    protected Payroll() {}

    /** 급여 = 기본급 + 수당(항목합) − 공제. 수당은 외부 주입이 아니라 근태 산출 결과를 받는다. */
    public static Payroll calculate(Long employeeId, YearMonth period,
                                    Money baseSalary, AllowanceBreakdown allowance, double insuranceRate) {
        Payroll p = new Payroll();
        p.employeeId = employeeId;
        p.period = period.toString();
        p.baseSalary = baseSalary.amount().longValueExact();
        p.allowance = allowance;
        long gross = p.baseSalary + allowance.total().amount().longValueExact();
        p.deduction = Math.round(gross * insuranceRate);
        p.netSalary = gross - p.deduction;
        p.register(new PayrollCalculatedEvent(null, employeeId, period.toString(), p.netSalary, Instant.now()));
        return p;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getPeriod() { return period; }
    public Money getBaseSalary() { return Money.of(baseSalary); }
    public AllowanceBreakdown getAllowance() { return allowance; }
    public Money getDeduction() { return Money.of(deduction); }
    public Money getNetSalary() { return Money.of(netSalary); }
}