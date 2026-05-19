package com.erp.hr.domain.vo;

import com.erp.common.domain.Money;
import jakarta.persistence.Embeddable;

/** 수당의 항목별 근거. 지급 확정 시점에 Payroll 에 박제(@Embedded)되어 감사 추적을 확보한다. */
@Embeddable
public class AllowanceBreakdown {

    private long overtimeAllowance;  // 연장수당
    private long nightAllowance;     // 야간수당
    private long holidayAllowance;   // 휴일수당

    protected AllowanceBreakdown() {}

    public AllowanceBreakdown(Money overtime, Money night, Money holiday) {
        this.overtimeAllowance = overtime.amount().longValueExact();
        this.nightAllowance    = night.amount().longValueExact();
        this.holidayAllowance  = holiday.amount().longValueExact();
    }

    public Money total() {
        return Money.of(overtimeAllowance + nightAllowance + holidayAllowance);
    }

    public Money getOvertimeAllowance() { return Money.of(overtimeAllowance); }
    public Money getNightAllowance()    { return Money.of(nightAllowance); }
    public Money getHolidayAllowance()  { return Money.of(holidayAllowance); }
}