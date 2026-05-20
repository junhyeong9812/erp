package com.erp.hr.application.port.outbound;

import java.time.LocalDate;

/** 휴일 판정 포트. 미구현 단계에선 주말 stub. (정식 공휴일 캘린더는 후속) */
public interface HolidayCalendar {
    boolean isHoliday(LocalDate date);
}