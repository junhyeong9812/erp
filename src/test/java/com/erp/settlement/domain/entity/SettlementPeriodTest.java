package com.erp.settlement.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class SettlementPeriodTest {

    @Test
    void 마감_후에는_다시_마감_불가() {
        SettlementPeriod p = SettlementPeriod.open(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        p.close();

        assertThatThrownBy(p::close).isInstanceOf(IllegalStateException.class);
    }
}