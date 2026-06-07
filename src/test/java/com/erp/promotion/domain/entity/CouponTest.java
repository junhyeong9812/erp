package com.erp.promotion.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @Test
    void issue_는_ISSUED_상태() {
        Coupon c = Coupon.issue("C1", 1L, 10, Money.ZERO, LocalDate.of(2030, 1, 1));

        assertThat(c.getCode()).isEqualTo("C1");
        assertThat(c.getStatus()).isEqualTo(Coupon.Status.ISSUED);
    }

    @Test
    void 비율_할인_적용() {
        Coupon c = Coupon.issue("C1", 1L, 10, Money.ZERO, LocalDate.of(2030, 1, 1));

        Money result = c.apply(Money.of(10_000));

        assertThat(result).isEqualTo(Money.of(9_000));
        assertThat(c.getStatus()).isEqualTo(Coupon.Status.USED);
    }

    @Test
    void 정액_할인_적용() {
        Coupon c = Coupon.issue("C2", 1L, 0, Money.of(3_000), LocalDate.of(2030, 1, 1));

        Money result = c.apply(Money.of(10_000));

        assertThat(result).isEqualTo(Money.of(7_000));
    }

    @Test
    void 할인액이_소계보다_크면_0_으로_클램프() {
        Coupon c = Coupon.issue("C3", 1L, 0, Money.of(99_999), LocalDate.of(2030, 1, 1));

        Money result = c.apply(Money.of(1_000));

        assertThat(result).isEqualTo(Money.of(0));
    }

    @Test
    void USED_쿠폰을_다시_apply_하면_예외() {
        Coupon c = Coupon.issue("C4", 1L, 10, Money.ZERO, LocalDate.of(2030, 1, 1));
        c.apply(Money.of(10_000));

        assertThatThrownBy(() -> c.apply(Money.of(5_000)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("사용 불가 쿠폰");
    }

    @Test
    void 비율이_100이면_전액_할인() {
        Coupon c = Coupon.issue("C5", 1L, 100, Money.ZERO, LocalDate.of(2030, 1, 1));

        Money result = c.apply(Money.of(5_000));

        assertThat(result).isEqualTo(Money.of(0));
    }
}