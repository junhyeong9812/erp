package com.erp.common.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuantityTest {

    @Test
    void 음수_생성은_IllegalArgument() {
        assertThatThrownBy(() -> new Quantity(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("quantity must be >= 0");
    }

    @Test
    void 값_0_은_유효() {
        Quantity q = Quantity.ZERO;
        assertThat(q.value()).isZero();
        assertThat(q.isZero()).isTrue();
    }

    @Test
    void add_는_값_합산() {
        assertThat(Quantity.of(3).add(Quantity.of(5))).isEqualTo(Quantity.of(8));
    }

    @Test
    void subtract_가_음수가_되면_예외() {
        assertThatThrownBy(() -> Quantity.of(3).subtract(Quantity.of(5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot subtract larger quantity");
    }

    @Test
    void subtract_결과가_0은_허용() {
        assertThat(Quantity.of(3).subtract(Quantity.of(3))).isEqualTo(Quantity.ZERO);
    }

    @Test
    void isGreaterThanOrEqual_동등값_포함() {
        assertThat(Quantity.of(5).isGreaterThanOrEqual(Quantity.of(5))).isTrue();
        assertThat(Quantity.of(5).isGreaterThanOrEqual(Quantity.of(6))).isFalse();
        assertThat(Quantity.of(6).isGreaterThanOrEqual(Quantity.of(5))).isTrue();
    }

    @Test
    void equals_와_hashCode() {
        assertThat(Quantity.of(7)).isEqualTo(Quantity.of(7));
        assertThat(Quantity.of(7).hashCode()).isEqualTo(Quantity.of(7).hashCode());
        assertThat(Quantity.of(7)).isNotEqualTo(Quantity.of(8));
    }

    @Test
    void toString_은_숫자_문자열() {
        assertThat(Quantity.of(42).toString()).isEqualTo("42");
    }
}