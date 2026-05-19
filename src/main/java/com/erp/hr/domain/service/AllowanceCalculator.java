package com.erp.hr.domain.service;

import com.erp.common.domain.Money;
import com.erp.hr.domain.vo.AllowanceBreakdown;
import com.erp.hr.domain.vo.WorkTimeBreakdown;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** WorkTimeBreakdown(분) + 통상시급 → 법정 가산수당(금액)으로 환산. 근로기준법 제56조. */
public class AllowanceCalculator {

    private static final int    DEFAULT_MONTHLY_MINUTES = 209 * 60; // 월 소정근로 209h
    private static final double OVERTIME_RATE = 0.5;   // 연장 50% 가산
    private static final double NIGHT_RATE    = 0.5;   // 야간 50% 가산
    private static final double HOLIDAY_RATE  = 0.5;   // 휴일 50% 가산(8h 이내)

    public AllowanceBreakdown calculate(Money monthlyBaseSalary, WorkTimeBreakdown bt) {
        BigDecimal hourlyWage = monthlyBaseSalary.amount()
                .divide(BigDecimal.valueOf(DEFAULT_MONTHLY_MINUTES / 60.0), 2, RoundingMode.HALF_UP);
        BigDecimal perMinute = hourlyWage.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);

        Money overtime = Money.of(round(perMinute, bt.overtimeMinutes(), OVERTIME_RATE));
        Money night    = Money.of(round(perMinute, bt.nightMinutes(),    NIGHT_RATE));
        Money holiday  = Money.of(round(perMinute, bt.holidayMinutes(),  HOLIDAY_RATE));
        return new AllowanceBreakdown(overtime, night, holiday);
    }

    private long round(BigDecimal perMinute, long minutes, double rate) {
        return perMinute.multiply(BigDecimal.valueOf(minutes))
                .multiply(BigDecimal.valueOf(rate))
                .setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}