package com.erp.hr.domain.service;

import com.erp.hr.domain.entity.WorkSchedule;
import com.erp.hr.domain.vo.WorkPeriod;
import com.erp.hr.domain.vo.WorkTimeBreakdown;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkTimeCalculationPolicyTest {

    private final WorkTimeCalculationPolicy policy = new WorkTimeCalculationPolicy();

    // 1일 소정근로 8h(480분)
    private final WorkSchedule schedule =
            WorkSchedule.of(1L, LocalTime.of(9, 0), LocalTime.of(18, 0), 480);

    @Test
    void 첫번째_자정넘김_야간8h_평일은_정규480_야간480_연장0() {
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 22, 0),
                LocalDateTime.of(2026, 4, 2, 6, 0));

        WorkTimeBreakdown bt = policy.calculate(wp, schedule, false);

        assertThat(bt.regularMinutes()).isEqualTo(480);   // min(480, 480)
        assertThat(bt.overtimeMinutes()).isEqualTo(0);     // max(0, 480-480)
        assertThat(bt.nightMinutes()).isEqualTo(480);
        assertThat(bt.holidayMinutes()).isEqualTo(0);
    }

    @Test
    void 두번째_연장_11h근무_평일은_정규480_연장180() {
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 9, 0),
                LocalDateTime.of(2026, 4, 1, 20, 0));      // 11h = 660분

        WorkTimeBreakdown bt = policy.calculate(wp, schedule, false);

        assertThat(bt.regularMinutes()).isEqualTo(480);
        assertThat(bt.overtimeMinutes()).isEqualTo(180);   // 660 - 480
        assertThat(bt.nightMinutes()).isEqualTo(0);
        assertThat(bt.holidayMinutes()).isEqualTo(0);
    }

    @Test
    void 세번째_연장겸야간_14시_익일02시는_연장240_야간240_중복() {
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 14, 0),
                LocalDateTime.of(2026, 4, 2, 2, 0));        // 12h = 720분

        WorkTimeBreakdown bt = policy.calculate(wp, schedule, false);

        assertThat(bt.regularMinutes()).isEqualTo(480);
        assertThat(bt.overtimeMinutes()).isEqualTo(240);    // 720 - 480
        assertThat(bt.nightMinutes()).isEqualTo(240);       // 22:00~02:00 — 연장과 겹친다
        assertThat(bt.holidayMinutes()).isEqualTo(0);
    }

    @Test
    void 네번째_휴일_11h근무는_전부_휴일근로로_분류된다() {
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 4, 9, 0),         // 토요일
                LocalDateTime.of(2026, 4, 4, 20, 0));       // 11h = 660분

        WorkTimeBreakdown bt = policy.calculate(wp, schedule, true);

        assertThat(bt.regularMinutes()).isEqualTo(0);
        assertThat(bt.overtimeMinutes()).isEqualTo(0);
        assertThat(bt.nightMinutes()).isEqualTo(0);         // 09~20시는 야간 아님
        assertThat(bt.holidayMinutes()).isEqualTo(660);     // total 전부 휴일근로
    }
}