package com.erp.hr.domain.vo;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkPeriodTest {

    @Test
    void 같은_날_주간근무는_야간_0분() {
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 9, 0),
                LocalDateTime.of(2026, 4, 1, 18, 0));   // 9h

        assertThat(wp.totalMinutes()).isEqualTo(540);
        assertThat(wp.nightMinutes()).isEqualTo(0);
    }

    @Test
    void 자정넘김_22시_익일06시는_총480분_전부_야간() {
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 22, 0),
                LocalDateTime.of(2026, 4, 2, 6, 0));     // 8h

        assertThat(wp.totalMinutes()).isEqualTo(480);
        assertThat(wp.nightMinutes()).isEqualTo(480);    // [22:00, 06:00) 전부
    }

    @Test
    void 연장겸야간_14시_익일02시는_총720분_야간240분() {
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 14, 0),
                LocalDateTime.of(2026, 4, 2, 2, 0));      // 12h

        assertThat(wp.totalMinutes()).isEqualTo(720);
        assertThat(wp.nightMinutes()).isEqualTo(240);     // 22:00~02:00 = 4h
    }

    @Test
    void 야간_시작경계_22시는_포함되고_그_직전분은_제외된다() {
        // nightMinutes 판정: t.isAfter(21:59) || t.isBefore(06:00)
        // 21:59 분 → 야간 아님 / 22:00 분 → 야간
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 21, 59),
                LocalDateTime.of(2026, 4, 1, 22, 1));      // 2분 구간

        assertThat(wp.totalMinutes()).isEqualTo(2);
        assertThat(wp.nightMinutes()).isEqualTo(1);        // 22:00 한 칸만 야간
    }

    @Test
    void 야간_종료경계_06시는_제외되고_그_직전분은_포함된다() {
        // 05:59 분 → 야간(isBefore 06:00) / 06:00 분 → 야간 아님
        WorkPeriod wp = new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 5, 59),
                LocalDateTime.of(2026, 4, 1, 6, 1));        // 2분 구간

        assertThat(wp.totalMinutes()).isEqualTo(2);
        assertThat(wp.nightMinutes()).isEqualTo(1);         // 05:59 한 칸만 야간
    }

    @Test
    void 퇴근이_출근보다_빠르면_생성_시점에_예외() {
        assertThatThrownBy(() -> new WorkPeriod(
                LocalDateTime.of(2026, 4, 1, 18, 0),
                LocalDateTime.of(2026, 4, 1, 9, 0)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}