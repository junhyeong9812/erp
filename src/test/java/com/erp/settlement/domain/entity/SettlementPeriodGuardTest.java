package com.erp.settlement.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class SettlementPeriodGuardTest {
    @Test
    void 마감된_기간에_assertOpen_호출시_예외() {
        SettlementPeriod p = SettlementPeriod.open(
                java.time.LocalDate.of(2026,1,1), java.time.LocalDate.of(2026,1,31));
        p.close();
        assertThatThrownBy(p::assertOpen)
                .isInstanceOf(com.erp.common.exception.ConflictException.class);
    }
}
