package com.erp.report.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricTest {

    @Test
    void of_로_생성하면_필드_매핑() {
        Metric m = Metric.of("payment.amount", "order:1", 5000.0);

        assertThat(m.getMetricName()).isEqualTo("payment.amount");
        assertThat(m.getDimensionKey()).isEqualTo("order:1");
        assertThat(m.getValue()).isEqualTo(5000.0);
    }

    @Test
    void assignId_로_식별자_부여() {
        Metric m = Metric.of("x", "y", 1.0);
        m.assignId(99L);
        assertThat(m.getId()).isEqualTo(99L);
    }
}