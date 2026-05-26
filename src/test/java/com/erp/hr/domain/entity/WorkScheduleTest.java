package com.erp.hr.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkScheduleTest {

    private final WorkSchedule schedule =
            WorkSchedule.of(1L, LocalTime.of(9, 0), LocalTime.of(18, 0), 480);

    @Test
    void 소정근로분과_직원ID_를_보관한다() {
        assertThat(schedule.getEmployeeId()).isEqualTo(1L);
        assertThat(schedule.getDailyContractMinutes()).isEqualTo(480);
    }

    @Test
    void 정각_09시_출근은_지각이_아니다_경계() {
        // isAfter(09:00) == false
        boolean late = schedule.isLate(LocalDateTime.of(2026, 4, 20, 9, 0));

        assertThat(late).isFalse();
    }

    @Test
    void 시각_09시_1분_출근은_지각이다() {
        boolean late = schedule.isLate(LocalDateTime.of(2026, 4, 20, 9, 1));

        assertThat(late).isTrue();
    }

    @Test
    void 시각_08시_59분_출근은_지각이_아니다() {
        boolean late = schedule.isLate(LocalDateTime.of(2026, 4, 20, 8, 59));

        assertThat(late).isFalse();
    }
}