package com.erp.settlement.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SellerSettlementTest {
    @Test
    void 매출에서_환불_수수료를_차감한_순지급액() {
        var s = com.erp.settlement.domain.entity.SellerSettlement.calculate(
                1L, 1L, Money.of(100_000), Money.of(20_000), Money.of(5_000));
        assertThat(s.getNetPayout()).isEqualTo(Money.of(75_000));
    }

    @Test
    void 순지급액이_음수이면_생성_차단() {
        assertThatThrownBy(() ->
                com.erp.settlement.domain.entity.SellerSettlement.calculate(
                        1L, 1L, Money.of(10_000), Money.of(20_000), Money.of(5_000)))
                .isInstanceOf(com.erp.common.exception.ConflictException.class);
    }

    @Test
    void 지급_완료_후_재지급_불가() {
        var s = com.erp.settlement.domain.entity.SellerSettlement.calculate(
                1L, 1L, Money.of(10_000), Money.ZERO, Money.ZERO);
        s.markPaid();
        assertThatThrownBy(s::markPaid)
                .isInstanceOf(com.erp.common.exception.ConflictException.class);
    }
}

