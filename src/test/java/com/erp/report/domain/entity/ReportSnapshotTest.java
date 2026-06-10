package com.erp.report.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReportSnapshotTest {

    @Test
    void generate_는_metric_을_그대로_담는다() {
        ReportSnapshot s = ReportSnapshot.generate(
                "DAILY_SALES",
                LocalDate.of(2026, 4, 20),
                Map.of("total", 12345.0, "count", 42.0));

        assertThat(s.getReportType()).isEqualTo("DAILY_SALES");
        assertThat(s.getTargetDate()).isEqualTo(LocalDate.of(2026, 4, 20));
        assertThat(s.metric("total")).isEqualTo(12345.0);
        assertThat(s.metric("count")).isEqualTo(42.0);
    }

    @Test
    void 없는_metric_이름은_0_반환() {
        ReportSnapshot s = ReportSnapshot.generate("X", LocalDate.now(), Map.of());
        assertThat(s.metric("unknown")).isZero();
    }

    @Test
    void generate_시_ReportGeneratedEvent_등록() {
        ReportSnapshot s = ReportSnapshot.generate(
                "DAILY_SALES",
                LocalDate.of(2026, 4, 20),
                Map.of("total", 10.0));

        assertThat(s.events()).hasSize(1);
        assertThat(s.events().get(0).getClass().getSimpleName())
                .isEqualTo("ReportGeneratedEvent");
    }

    @Test
    void 입력_Map_수정해도_스냅샷_내부_Map_불변() {
        var input = new java.util.HashMap<String, Double>();
        input.put("a", 1.0);
        ReportSnapshot s = ReportSnapshot.generate("X", LocalDate.now(), input);

        input.put("b", 2.0);   // 외부에서 변경
        assertThat(s.getMetrics()).doesNotContainKey("b");   // 복사본이므로 영향 없음
    }
}