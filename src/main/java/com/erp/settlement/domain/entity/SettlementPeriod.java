package com.erp.settlement.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.settlement.domain.exception.SettlementErrorCode;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "settlement_period")
public class SettlementPeriod extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected SettlementPeriod() {}

    public static SettlementPeriod open(LocalDate start, LocalDate end) {
        SettlementPeriod p = new SettlementPeriod();
        p.startDate = start;
        p.endDate = end;
        p.status = Status.OPEN;
        return p;
    }

    public void close() {
        if (status != Status.OPEN) throw new IllegalStateException("OPEN 상태만 마감 가능");
        this.status = Status.CLOSED;
    }

    /** 전표 생성 진입점에서 호출. 마감된 기간에 전표 추가 시 409 CONFLICT. */
    public void assertOpen() {
        if (status != Status.OPEN) {
            throw new com.erp.common.exception.ConflictException(
                    SettlementErrorCode.PERIOD_CLOSED,
                    "periodId=" + id + " status=" + status);
        }
    }

    public boolean contains(java.time.LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public boolean isOpen() { return status == Status.OPEN; }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Status getStatus() { return status; }

    public enum Status { OPEN, CLOSED, SETTLED }
}