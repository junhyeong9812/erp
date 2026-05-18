package com.erp.production.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProductionPlanTest {

    @Test
    void plan_으로_생성하면_필드_세팅() {
        ProductionPlan p = ProductionPlan.plan(100L, LocalDate.of(2026, 5, 1), 200);
        p.assignId(1L);

        assertThat(p.getId()).isEqualTo(1L);
        assertThat(p.getProductId()).isEqualTo(100L);
        assertThat(p.getPlannedQuantity()).isEqualTo(200);
    }
}