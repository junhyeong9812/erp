package com.erp.hr.domain.vo;

/** 근로시간을 가산 유형별로 분해한 결과. 계산 중간산물(파생값). 영속되지 않는다. */
public record WorkTimeBreakdown(
        long regularMinutes,    // 소정 근로
        long overtimeMinutes,   // 연장 (8h/주40h 초과)
        long nightMinutes,      // 야간 (22~06)
        long holidayMinutes) {  // 휴일 근로
}