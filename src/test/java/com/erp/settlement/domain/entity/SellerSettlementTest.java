package com.erp.settlement.domain.entity;

import com.erp.common.domain.Money;
import com.erp.common.exception.ConflictException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerSettlementTest {

    @Test
    void calculate_는_매출에서_환불과_수수료를_뺀_순지급액을_계산() {
        SellerSettlement s = SellerSettlement.calculate(
                1L, 1L,
                Money.of(100_000), Money.of(20_000), Money.of(5_000));

        assertThat(s.getGrossSales()).isEqualTo(Money.of(100_000));
        assertThat(s.getRefundAmount()).isEqualTo(Money.of(20_000));
        assertThat(s.getFeeAmount()).isEqualTo(Money.of(5_000));
        assertThat(s.getNetPayout()).isEqualTo(Money.of(75_000));
        assertThat(s.getStatus()).isEqualTo(SellerSettlement.Status.CALCULATED);
    }

    @Test
    void 환불_수수료가_매출보다_크면_ConflictException() {
        assertThatThrownBy(() -> SellerSettlement.calculate(
                1L, 1L,
                Money.of(10_000), Money.of(20_000), Money.of(5_000)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("net=");
    }

    @Test
    void gross_와_정확히_같은_차감은_0원_지급으로_허용() {
        SellerSettlement s = SellerSettlement.calculate(
                1L, 1L,
                Money.of(10_000), Money.of(5_000), Money.of(5_000));

        assertThat(s.getNetPayout()).isEqualTo(Money.ZERO);
    }

    @Test
    void markPaid_는_CALCULATED_에서_PAID_로_전이() {
        SellerSettlement s = SellerSettlement.calculate(
                1L, 1L, Money.of(10_000), Money.ZERO, Money.ZERO);

        s.markPaid();

        assertThat(s.getStatus()).isEqualTo(SellerSettlement.Status.PAID);
    }

    @Test
    void 이미_PAID_인_정산을_재지급_하면_ConflictException() {
        SellerSettlement s = SellerSettlement.calculate(
                1L, 1L, Money.of(10_000), Money.ZERO, Money.ZERO);
        s.markPaid();

        assertThatThrownBy(s::markPaid)
                .isInstanceOf(ConflictException.class);
    }
}