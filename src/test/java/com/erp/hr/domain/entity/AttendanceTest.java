package com.erp.hr.domain.entity;

import com.erp.hr.domain.vo.WorkPeriod;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttendanceTest {

    @Test
    void checkIn_은_출근시각과_직원ID_를_보관() {
        Attendance a = Attendance.checkIn(42L, LocalDateTime.of(2026, 4, 20, 9, 0));

        assertThat(a.getEmployeeId()).isEqualTo(42L);
        assertThat(a.getCheckIn()).isEqualTo(LocalDateTime.of(2026, 4, 20, 9, 0));
        assertThat(a.getCheckOut()).isNull();
    }

    @Test
    void checkOut_전이면_toWorkPeriod_는_빈값() {
        Attendance a = Attendance.checkIn(1L, LocalDateTime.of(2026, 4, 20, 9, 0));

        assertThat(a.toWorkPeriod()).isEmpty();
    }

    @Test
    void checkOut_후에는_toWorkPeriod_가_채워진다() {
        Attendance a = Attendance.checkIn(1L, LocalDateTime.of(2026, 4, 20, 9, 0));

        a.checkOut(LocalDateTime.of(2026, 4, 20, 18, 0));

        assertThat(a.toWorkPeriod()).isPresent();
        WorkPeriod wp = a.toWorkPeriod().orElseThrow();
        assertThat(wp.totalMinutes()).isEqualTo(540);  // 9h
    }

    @Test
    void 자정_넘는_근무도_checkOut_날짜로_자연표현() {
        Attendance a = Attendance.checkIn(1L, LocalDateTime.of(2026, 4, 1, 22, 0));

        a.checkOut(LocalDateTime.of(2026, 4, 2, 6, 0));  // 익일 06:00

        assertThat(a.toWorkPeriod().orElseThrow().totalMinutes()).isEqualTo(480);  // 8h
    }

    @Test
    void 퇴근이_출근보다_빠르면_예외() {
        Attendance a = Attendance.checkIn(1L, LocalDateTime.of(2026, 4, 20, 18, 0));

        assertThatThrownBy(() -> a.checkOut(LocalDateTime.of(2026, 4, 20, 9, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}