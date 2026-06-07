package com.erp.promotion.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointLedgerTest {

    @Test
    void EARN_타입_생성() {
        PointLedger l = PointLedger.of(1L, 500, PointLedger.Type.EARN, 100L);
        l.assignId(1L);

        assertThat(l.getId()).isEqualTo(1L);
    }

    @Test
    void 네_가지_타입_모두_생성_가능() {
        assertThat(PointLedger.of(1L, 100, PointLedger.Type.EARN, 1L)).isNotNull();
        assertThat(PointLedger.of(1L, -100, PointLedger.Type.USE, 1L)).isNotNull();
        assertThat(PointLedger.of(1L, -100, PointLedger.Type.EXPIRE, 1L)).isNotNull();
        assertThat(PointLedger.of(1L, 100, PointLedger.Type.REFUND, 1L)).isNotNull();
    }
}