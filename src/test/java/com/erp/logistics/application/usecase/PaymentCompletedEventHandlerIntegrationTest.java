package com.erp.logistics.application.usecase;

import com.erp.logistics.application.port.outbound.ShipmentRepository;
import com.erp.logistics.domain.entity.Shipment;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import java.time.Duration;

@SpringBootTest
@ApplicationModuleTest
class PaymentCompletedEventHandlerIntegrationTest {

    @Autowired ApplicationEventPublisher publisher;
    @Autowired ShipmentRepository shipmentRepository;

    @Test
    void PaymentCompletedEvent_를_받으면_해당_주문의_Shipment_가_생성된다() {
        Long orderId = 4242L;

        publisher.publishEvent(new PaymentCompletedEvent(1L, orderId, 10_000L, Instant.now()));

        // @ApplicationModuleListener 는 비동기 트랜잭션 커밋 후 실행되므로 awaitility 대기
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() ->
                assertThat(shipmentRepository.findByOrderId(orderId))
                        .isPresent()
                        .get()
                        .extracting(Shipment::getStatus)
                        .isEqualTo(Shipment.Status.PREPARING)
        );
    }
}