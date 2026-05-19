package com.erp.hr.domain.vo;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 출퇴근 한 구간의 "물리적" 시간 계산만 책임진다. 영속되지 않는다.
 * 정책 의존 계산(연장 판정 등)은 여기에 두지 않는다 — WorkTimeCalculationPolicy 가 담당.
 */
public record WorkPeriod(LocalDateTime start, LocalDateTime end) {

    public WorkPeriod {
        if (end.isBefore(start)) throw new IllegalArgumentException("end < start");
    }

    /** 총 근로시간(분). 자정 넘김은 날짜 차이로 자연 계산된다. */
    public long totalMinutes() {
        return Duration.between(start, end).toMinutes();
    }

    /** 야간(22:00~06:00) 구간에 속한 분. 법정 고정 구간이므로 VO 가 직접 계산한다. */
    public long nightMinutes() {
        long minutes = 0;
        LocalDateTime cursor = start;
        while (cursor.isBefore(end)) {
            LocalTime t = cursor.toLocalTime();
            boolean isNight = t.isAfter(LocalTime.of(21, 59)) || t.isBefore(LocalTime.of(6, 0));
            if (isNight) minutes++;
            cursor = cursor.plusMinutes(1);
        }
        return minutes; // 구현 시 분단위 루프 대신 구간 교집합으로 최적화 가능
    }
}