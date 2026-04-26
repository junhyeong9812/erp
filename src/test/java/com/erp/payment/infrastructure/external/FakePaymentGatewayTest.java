package com.erp.payment.infrastructure.external;

import com.erp.common.domain.Money;
import com.erp.payment.application.port.outbound.PaymentGateway;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FakePaymentGatewayTest {

    private final FakePaymentGateway gateway = new FakePaymentGateway();

    @Test
    void charge_는_항상_success_true_이며_pgTransactionId_가_FAKE_prefix() {
        PaymentGateway.Result r = gateway.charge(1L, Money.of(1000), "CARD");

        assertThat(r.success()).isTrue();
        assertThat(r.pgTransactionId()).startsWith("FAKE-");
    }

    @Test
    void refund_는_항상_success_true_이며_pgTransactionId_가_REFUND_prefix() {
        PaymentGateway.Result r = gateway.refund("FAKE-xxx", Money.of(500));

        assertThat(r.success()).isTrue();
        assertThat(r.pgTransactionId()).startsWith("REFUND-");
    }
}