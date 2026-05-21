package com.erp.hr.infrastructure.calendar;

import com.erp.hr.application.port.outbound.HolidayCalendar;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/** stub: 주말만 휴일로 판정. 정식 공휴일 캘린더는 후속 과제 — "정식 정책"으로 굳히지 말 것(codex). */
@Component
public class WeekendHolidayCalendar implements HolidayCalendar {
    @Override public boolean isHoliday(LocalDate date) {
        DayOfWeek d = date.getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }
}