package com.erp.common.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void of_로_생성한_Money_는_정수_스케일() {
        Money m = Money.of(1234);
        assertThat(m.amount()).isEqualTo(new BigDecimal("1234"));
        assertThat(m.amount().scale()).isZero();
    }

    @Test
    void 생성자에_null_전달하면_NPE() {
        assertThatThrownBy(() -> new Money(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("amount must not be null");
    }

    @Test
    void 소수점은_HALF_UP_으로_반올림() {
        Money m1 = new Money(new BigDecimal("10.4"));
        Money m2 = new Money(new BigDecimal("10.5"));
        assertThat(m1).isEqualTo(Money.of(10));
        assertThat(m2).isEqualTo(Money.of(11));
    }

    @Test
    void add_는_새_인스턴스를_반환_원본_불변() {
        Money a = Money.of(100);
        Money b = Money.of(50);
        Money sum = a.add(b);

        assertThat(sum).isEqualTo(Money.of(150));
        assertThat(a).isEqualTo(Money.of(100));   // 원본 그대로
        assertThat(sum).isNotSameAs(a);           // 다른 인스턴스
    }

    @Test
    void subtract_는_음수도_허용() {
        // Money 는 음수 허용 (환불/차감에 필요). isNegative 로 체크는 호출자 책임.
        Money result = Money.of(100).subtract(Money.of(200));
        assertThat(result.isNegative()).isTrue();
    }

    @Test
    void multiply_는_정수배() {
        assertThat(Money.of(100).multiply(3)).isEqualTo(Money.of(300));
    }

    @Test
    void ZERO_상수는_덧셈_항등원() {
        Money m = Money.of(500);
        assertThat(m.add(Money.ZERO)).isEqualTo(m);
    }

    @Test
    void isGreaterThan_동등값은_false() {
        assertThat(Money.of(100).isGreaterThan(Money.of(100))).isFalse();
        assertThat(Money.of(101).isGreaterThan(Money.of(100))).isTrue();
    }

    @Test
    void equals_는_scale_달라도_값이_같으면_동등() {
        // BigDecimal.equals 는 scale 민감하지만 Money 는 compareTo 기반이어야 함
        Money a = new Money(new BigDecimal("100"));
        Money b = new Money(new BigDecimal("100.00"));
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void toString_은_KRW_포맷() {
        assertThat(Money.of(1234).toString()).isEqualTo("1234 KRW");
    }
}