package com.erp.integration;

import com.erp.logistics.application.port.outbound.ShipmentRepository;
import com.erp.notification.application.port.outbound.NotificationRepository;
import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.inbound.PaymentUseCase;
import com.erp.promotion.application.port.outbound.PointRepository;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.domain.entity.Ledger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentRippleTest {

    @Autowired private PaymentUseCase paymentUseCase;
    @Autowired private LedgerRepository ledgerRepository;
    @Autowired private ShipmentRepository shipmentRepository;
    @Autowired private PointRepository pointRepository;
    @Autowired private NotificationRepository notificationRepository;

    @Test
    void 결제_1회에_네_모듈이_모두_반응한다() {
        Long orderId = 1234L;
        paymentUseCase.requestPayment(new RequestPaymentCommand(orderId, "CARD", 10000));

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            // Settlement: 매출 전표
            assertThat(ledgerRepository.findByPeriodAndType(1L, Ledger.Type.SALES)).isNotEmpty();
            // Logistics: 출고 지시
            assertThat(shipmentRepository.findByOrderId(orderId)).isPresent();
            // Promotion: 포인트 적립 (1% = 100P)
            assertThat(pointRepository.findActiveByCustomer(orderId)).isNotEmpty();
            // Notification: 알림 발송 이력
            assertThat(notificationRepository.findById(1L)).isPresent();
        });
    }
}