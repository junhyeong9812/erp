package com.erp.payment.domain.entity;

import com.erp.common.domain.Money;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTest {

    @Test
    void request_하면_PENDING_상태() {
        Payment p = Payment.request(1L, Payment.Method.CARD, Money.of(1000));

        assertThat(p.getOrderId()).isEqualTo(1L);
        assertThat(p.getAmount()).isEqualTo(Money.of(1000));
        assertThat(p.getStatus()).isEqualTo(Payment.Status.PENDING);
    }

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
    void complete_는_PENDING_이_아니면_불가() {
        Payment p = Payment.request(1L, Payment.Method.CARD, Money.of(1000));
        p.complete("PG-1");

        assertThatThrownBy(() -> p.complete("PG-2"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 가능 상태");
    }

    @Test
    void fail_은_FAILED_로_상태_전이() {
        Payment p = Payment.request(1L, Payment.Method.CARD, Money.of(1000));
        p.fail("card declined");

        assertThat(p.getStatus()).isEqualTo(Payment.Status.FAILED);
    }

    @Test
    void 실패한_Payment_는_complete_불가() {
        Payment p = Payment.request(1L, Payment.Method.CARD, Money.of(1000));
        p.fail("x");

        assertThatThrownBy(() -> p.complete("PG-later"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void PaymentCompletedEvent_필드_검증() {
        Payment p = Payment.request(1L, Payment.Method.BANK, Money.of(1500));
        p.assignId(77L);

        p.complete("PG-XYZ");

        PaymentCompletedEvent e = p.events().stream()
                .filter(PaymentCompletedEvent.class::isInstance)
                .map(PaymentCompletedEvent.class::cast)
                .findFirst()
                .orElseThrow();

        assertThat(e.orderId()).isEqualTo(1L);
        assertThat(e.amount()).isEqualTo(1500L);
        assertThat(e.occurredAt()).isNotNull();
    }

    @Test
    void Method_enum_모두_존재() {
        assertThat(Payment.Method.values()).containsExactlyInAnyOrder(
                Payment.Method.CARD, Payment.Method.BANK, Payment.Method.VIRTUAL_ACCOUNT);
    }
}