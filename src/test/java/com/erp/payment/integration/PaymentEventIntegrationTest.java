package com.erp.payment.integration;

import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.inbound.PaymentUseCase;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class PaymentEventIntegrationTest {

    @TestConfiguration
    static class CaptureConfig {
        @Bean
        EventCapture capture() { return new EventCapture(); }
    }

    static class EventCapture {
        final CopyOnWriteArrayList<PaymentCompletedEvent> completed = new CopyOnWriteArrayList<>();

        @EventListener
        void on(PaymentCompletedEvent e) { completed.add(e); }
    }

    @Autowired PaymentUseCase paymentUseCase;
    @Autowired EventCapture capture;

    @Test
    void 결제_요청_성공_시_PaymentCompletedEvent_리스너_전달() {
        paymentUseCase.requestPayment(new RequestPaymentCommand(1L, "CARD", 1000));

        await().atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> assertThat(capture.completed)
                        .extracting(PaymentCompletedEvent::orderId)
                        .contains(1L));
    }

    @Test
    void 이벤트에_실린_금액은_Command_와_동일() {
        paymentUseCase.requestPayment(new RequestPaymentCommand(2L, "BANK", 2500));

        await().atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> assertThat(capture.completed)
                        .filteredOn(e -> e.orderId() == 2L)
                        .extracting(PaymentCompletedEvent::amount)
                        .contains(2500L));
    }
}