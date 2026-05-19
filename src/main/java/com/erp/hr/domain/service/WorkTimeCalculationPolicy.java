package com.erp.hr.domain.service;

import com.erp.hr.domain.entity.WorkSchedule;
import com.erp.hr.domain.vo.WorkPeriod;
import com.erp.hr.domain.vo.WorkTimeBreakdown;

/**
 * 정책 의존 계산을 캡슐화. 연장/휴일 판정은 근무계약·스케줄·휴일여부에 의존하므로
 * VO 가 아닌 정책 객체가 담당한다.
 */
public class WorkTimeCalculationPolicy {

    public WorkTimeBreakdown calculate(WorkPeriod period, WorkSchedule schedule, boolean isHoliday) {
        long total = period.totalMinutes();
        long night = period.nightMinutes();

        if (isHoliday) {
            return new WorkTimeBreakdown(0, 0, night, total);
        }
        long contract = schedule.getDailyContractMinutes();
        long regular  = Math.min(total, contract);
        long overtime = Math.max(0, total - contract);
        return new WorkTimeBreakdown(regular, overtime, night, 0);
    }
}