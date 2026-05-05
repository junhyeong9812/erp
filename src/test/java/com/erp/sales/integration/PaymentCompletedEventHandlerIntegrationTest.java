package com.erp.sales.integration;

import com.erp.common.messaging.EventBus;
import com.erp.common.persistence.InMemoryRepository;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import com.erp.sales.application.port.outbound.SalesOrderRepository;
import com.erp.sales.domain.entity.SalesOrder;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PHASE04 정정 — PaymentCompletedEvent 가 sales 모듈의
 * {@link com.erp.sales.application.usecase.PaymentCompletedEventHandler} 에 의해
 * 수신되어 SalesOrder 가 PLACED → CONFIRMED 로 자동 전이되는지 end-to-end 검증.
 */
@SpringBootTest
class PaymentCompletedEventHandlerIntegrationTest {

    @Autowired EventBus eventBus;
    @Autowired SalesOrderUseCase salesOrderUseCase;
    @Autowired SalesOrderRepository salesOrderRepo;
    @Autowired PlatformTransactionManager txManager;

    @AfterEach
    void cleanStores() {
        ((InMemoryRepository<?, ?>) salesOrderRepo).clear();
    }

    private void publishInTx(com.erp.common.domain.DomainEvent event) {
        new TransactionTemplate(txManager).executeWithoutResult(s -> eventBus.publish(event));
    }

    @Test
    void PaymentCompletedEvent_수신시_해당_주문이_CONFIRMED_로_전이된다() {
        Long orderId = salesOrderUseCase.placeOrder(new PlaceOrderCommand(
                1L, null, List.of(new PlaceOrderCommand.Line(100L, 1, 1000L))));
        assertThat(salesOrderRepo.findById(orderId).orElseThrow().getStatus())
                .isEqualTo(SalesOrder.Status.PLACED);

        publishInTx(new PaymentCompletedEvent(50L, orderId, 1000L, Instant.now()));

        Awaitility.await().atMost(Duration.ofSeconds(3))
                .untilAsserted(() ->
                        assertThat(salesOrderRepo.findById(orderId).orElseThrow().getStatus())
                                .isEqualTo(SalesOrder.Status.CONFIRMED));
    }

    @Test
    void 이미_CONFIRMED_상태면_재수신해도_예외_없이_무시() {
        Long orderId = salesOrderUseCase.placeOrder(new PlaceOrderCommand(
                1L, null, List.of(new PlaceOrderCommand.Line(100L, 1, 1000L))));
        salesOrderUseCase.confirm(orderId);   // 미리 CONFIRMED 로 만들어둠

        // 재수신 — IllegalStateException 이 핸들러에서 swallow 되어야 한다.
        publishInTx(new PaymentCompletedEvent(50L, orderId, 1000L, Instant.now()));

        // 충분히 대기해도 예외로 인해 컨텍스트가 깨지지 않으며 상태는 CONFIRMED 유지
        Awaitility.await().atMost(Duration.ofSeconds(2))
                .untilAsserted(() ->
                        assertThat(salesOrderRepo.findById(orderId).orElseThrow().getStatus())
                                .isEqualTo(SalesOrder.Status.CONFIRMED));
    }
}
