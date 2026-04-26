package com.erp.payment.domain.entity;

import com.erp.common.domain.Money;
import com.erp.payment.domain.event.RefundCompletedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefundTest {

    @Test
    void request_하면_PENDING() {
        Refund r = Refund.request(10L, 1L, Money.of(500), "반품");

        assertThat(r.getPaymentId()).isEqualTo(10L);
        assertThat(r.getOrderId()).isEqualTo(1L);
        assertThat(r.getAmount()).isEqualTo(Money.of(500));
        assertThat(r.getStatus()).isEqualTo(Refund.Status.PENDING);
    }

    @Test
    void complete_하면_COMPLETED_및_RefundCompletedEvent_발행() {
        Refund r = Refund.request(10L, 1L, Money.of(500), "반품");
        r.assignId(99L);

        r.complete();

        assertThat(r.getStatus()).isEqualTo(Refund.Status.COMPLETED);
        assertThat(r.events())
                .hasAtLeastOneElementOfType(RefundCompletedEvent.class);
    }

    @Test
    void RefundCompletedEvent_필드_검증() {
        Refund r = Refund.request(10L, 1L, Money.of(500), "고객 요청");
        r.assignId(99L);

        r.complete();

        RefundCompletedEvent e = r.events().stream()
                .filter(RefundCompletedEvent.class::isInstance)
                .map(RefundCompletedEvent.class::cast)
                .findFirst()
                .orElseThrow();

        assertThat(e.refundId()).isEqualTo(99L);
        assertThat(e.paymentId()).isEqualTo(10L);
        assertThat(e.orderId()).isEqualTo(1L);
        assertThat(e.amount()).isEqualTo(500L);
    }
}