package com.erp.integration;

import com.erp.common.domain.Quantity;
import com.erp.common.support.IdGenerator;
import com.erp.crm.application.dto.command.RegisterCustomerCommand;
import com.erp.crm.application.port.inbound.CustomerUseCase;
import com.erp.inventory.application.dto.command.ReceiveStockCommand;
import com.erp.inventory.application.port.inbound.StockUseCase;
import com.erp.logistics.application.dto.command.DispatchShipmentCommand;
import com.erp.logistics.application.port.inbound.ShipmentUseCase;
import com.erp.logistics.application.port.outbound.ShipmentRepository;
import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.inbound.PaymentUseCase;
import com.erp.report.application.port.outbound.MetricRepository;
import com.erp.sales.application.dto.command.CreateQuoteCommand;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.port.inbound.QuoteUseCase;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EndToEndOrderFlowTest {

    @Autowired CustomerUseCase customerUseCase;
    @Autowired StockUseCase stockUseCase;
    @Autowired QuoteUseCase quoteUseCase;
    @Autowired SalesOrderUseCase salesOrderUseCase;
    @Autowired PaymentUseCase paymentUseCase;
    @Autowired ShipmentUseCase shipmentUseCase;
    @Autowired ShipmentRepository shipmentRepository;
    @Autowired MetricRepository metricRepository;

    @Test
    void 고객등록_입고_견적_수주_결제_출고_리포트() throws Exception {
        // 1. 고객 등록
        Long customerId = customerUseCase.register(new RegisterCustomerCommand(
                "C-E2E", "E2E Corp", "010", 1L, 1_000_000));

        // 2. 재고 입고
        stockUseCase.receive(new ReceiveStockCommand(100L, 1L, 100, "e2e"));

        // 3. 견적 발행
        Long quoteId = quoteUseCase.createQuote(new CreateQuoteCommand(
                customerId,
                List.of(new CreateQuoteCommand.Line(100L, 10, 1000)),
                LocalDate.now().plusDays(7)));

        // 4. 수주 (이 시점에 Inventory 가 자동 예약)
        Long orderId = salesOrderUseCase.placeOrder(new PlaceOrderCommand(customerId, quoteId, List.of(
                new PlaceOrderCommand.Line(100L, 10, 1000)
        )));

        // 5. 결제 — Settlement/Logistics/Promotion/Notification 파급
        paymentUseCase.requestPayment(new RequestPaymentCommand(orderId, "CARD", 10000));

        // 6. 출고 지시가 자동 생성될 때까지 대기
        Long shipmentId = Awaitility.await().atMost(Duration.ofSeconds(3)).until(
                () -> shipmentRepository.findByOrderId(orderId).map(s -> s.getId()).orElse(null),
                id -> id != null);

        // 7. 배차 + 배송 시작
        shipmentUseCase.dispatch(new DispatchShipmentCommand(shipmentId, "DRV-001", "TRK-001"));

        // 8. 리포트 Metric 이 쌓였는지 검증
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAsserted(() ->
                assertThat(metricRepository.findByName("payment.amount")).isNotEmpty()
        );
    }
}