package com.erp.payment.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void create_하면_PENDING_상태이며_paidAmount_0() {
        Order o = Order.create("ORD-1", 100L, Money.of(1000));

        assertThat(o.getOrderNumber()).isEqualTo("ORD-1");
        assertThat(o.getCustomerId()).isEqualTo(100L);
        assertThat(o.getTotalAmount()).isEqualTo(Money.of(1000));
        assertThat(o.getPaidAmount()).isEqualTo(Money.ZERO);
        assertThat(o.getRemainingAmount()).isEqualTo(Money.of(1000));
        assertThat(o.getStatus()).isEqualTo(Order.Status.PENDING);
    }

    @Test
    void 부분_결제_누적_후_총액_도달하면_PAID() {
        Order o = Order.create("ORD-1", 100L, Money.of(1000));
        o.applyPayment(Money.of(300));

        assertThat(o.getStatus()).isEqualTo(Order.Status.PENDING);
        assertThat(o.getRemainingAmount()).isEqualTo(Money.of(700));

        o.applyPayment(Money.of(700));

        assertThat(o.getStatus()).isEqualTo(Order.Status.PAID);
        assertThat(o.getRemainingAmount()).isEqualTo(Money.ZERO);
    }

    @Test
    void 총액_초과_결제_시_IllegalStateException() {
        Order o = Order.create("ORD-1", 100L, Money.of(1000));
        o.applyPayment(Money.of(900));

        assertThatThrownBy(() -> o.applyPayment(Money.of(200)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("총액 초과");
    }

    @Test
    void refund_하면_paidAmount_감소하고_0_되면_CANCELLED() {
        Order o = Order.create("ORD-1", 100L, Money.of(1000));
        o.applyPayment(Money.of(1000));

        o.refund(Money.of(400));
        assertThat(o.getPaidAmount()).isEqualTo(Money.of(600));
        assertThat(o.getStatus()).isEqualTo(Order.Status.PAID);

        o.refund(Money.of(600));
        assertThat(o.getPaidAmount()).isEqualTo(Money.ZERO);
        assertThat(o.getStatus()).isEqualTo(Order.Status.CANCELLED);
    }

    @Test
    void 결제액보다_많이_환불하면_예외() {
        Order o = Order.create("ORD-1", 100L, Money.of(1000));
        o.applyPayment(Money.of(500));

        assertThatThrownBy(() -> o.refund(Money.of(600)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("환불 금액이 결제액보다 큼");
    }
}