package com.erp.hr.domain.vo;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WorkTimeBreakdownTest {

    @Test
    void sum_은_항목별로_누적합산한다() {
        WorkTimeBreakdown a = new WorkTimeBreakdown(480, 60, 30, 0);
        WorkTimeBreakdown b = new WorkTimeBreakdown(480, 120, 240, 0);
        WorkTimeBreakdown c = new WorkTimeBreakdown(0, 0, 0, 660);

        WorkTimeBreakdown total = WorkTimeBreakdown.sum(List.of(a, b, c));

        assertThat(total.regularMinutes()).isEqualTo(960);
        assertThat(total.overtimeMinutes()).isEqualTo(180);
        assertThat(total.nightMinutes()).isEqualTo(270);
        assertThat(total.holidayMinutes()).isEqualTo(660);
    }

    @Test
    void 빈_리스트의_합은_EMPTY() {
        WorkTimeBreakdown total = WorkTimeBreakdown.sum(List.of());

        assertThat(total).isEqualTo(WorkTimeBreakdown.EMPTY);
    }
}