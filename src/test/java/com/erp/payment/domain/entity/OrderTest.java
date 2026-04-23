package com.erp.payment.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    void 부분_결제_누적() {
        Order o = Order.create("ORD-1", 100L, Money.of(1000));
        o.applyPayment(Money.of(300));
        o.applyPayment(Money.of(700));

        assertThat(o.getStatus()).isEqualTo(Order.Status.PAID);
        assertThat(o.getRemainingAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void 총액_초과_결제_불가() {
        Order o = Order.create("ORD-1", 100L, Money.of(1000));
        o.applyPayment(Money.of(900));
        assertThatThrownBy(() -> o.applyPayment(Money.of(200)))
                .isInstanceOf(IllegalStateException.class);
    }
}