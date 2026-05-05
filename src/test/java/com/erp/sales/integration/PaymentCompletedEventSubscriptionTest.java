package com.erp.sales.integration;

import com.erp.common.messaging.SpringEventBus;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        SpringEventBus.class,
        PaymentCompletedEventSubscriptionTest.SalesPaymentListener.class
})
class PaymentCompletedEventSubscriptionTest {

    @Component
    static class SalesPaymentListener {
        final List<PaymentCompletedEvent> received = new ArrayList<>();
        @EventListener
        public void on(PaymentCompletedEvent e) { received.add(e); }
    }

    @Autowired SpringEventBus bus;
    @Autowired SalesPaymentListener listener;

    @Test
    void PaymentCompletedEvent_발행_시_구독자에_전달() {
        bus.publish(new PaymentCompletedEvent(1L, 10L, 5000L, Instant.now()));

        assertThat(listener.received).hasSize(1);
        assertThat(listener.received.get(0).orderId()).isEqualTo(10L);
        assertThat(listener.received.get(0).amount()).isEqualTo(5000L);
    }
}