package com.erp.payment.domain.entity;

import com.erp.common.domain.Money;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PaymentTest {

    @Test
    void 결제_완료_시_이벤트_발행() {
        Payment p = Payment.request(1L, Payment.Method.CARD, Money.of(1000));
        p.assignId(10L);

        p.complete("PG-TX-001");

        assertThat(p.getStatus()).isEqualTo(Payment.Status.COMPLETED);
        assertThat(p.events())
                .hasAtLeastOneElementOfType(PaymentCompletedEvent.class);
    }

    @Test
    void PENDING_이_아니면_complete_불가() {
        Payment p = Payment.request(1L, Payment.Method.CARD, Money.of(1000));
        p.complete("PG-1");

        assertThatThrownBy(() -> p.complete("PG-2"))
                .isInstanceOf(IllegalStateException.class);
    }
}