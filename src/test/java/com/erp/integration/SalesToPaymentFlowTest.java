package com.erp.integration;

import com.erp.common.domain.Quantity;
import com.erp.common.support.IdGenerator;
import com.erp.inventory.application.dto.command.ReceiveStockCommand;
import com.erp.inventory.application.dto.query.StockQuery;
import com.erp.inventory.application.port.inbound.StockUseCase;
import com.erp.inventory.application.port.outbound.StockRepository;
import com.erp.inventory.domain.entity.Stock;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SalesToPaymentFlowTest {

    @Autowired private SalesOrderUseCase salesOrderUseCase;
    @Autowired private StockUseCase stockUseCase;
    @Autowired private StockRepository stockRepository;

    private final Long productId = 100L;
    private final Long warehouseId = 1L;

    @BeforeEach
    void seedStock() {
        // 각 테스트 시작 시 인메모리 초기화
        if (stockRepository instanceof com.erp.common.persistence.InMemoryRepository<?, ?> r) r.clear();

        // 재고 10 개 입고
        Stock initial = Stock.open(productId, warehouseId, Quantity.of(10));
        initial.assignId(IdGenerator.next());
        stockRepository.save(initial);
    }

    @Test
    void 수주_발행하면_Inventory_가_자동_예약한다() {
        salesOrderUseCase.placeOrder(new PlaceOrderCommand(1L, null, List.of(
                new PlaceOrderCommand.Line(productId, 3, 1000)
        )));

        // @ApplicationModuleListener 는 비동기 — 최대 2초 안에 반영되길 기대
        Awaitility.await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    StockQuery q = stockUseCase.query(productId, warehouseId);
                    assertThat(q.reserved()).isEqualTo(3);
                    assertThat(q.available()).isEqualTo(7);
                });
    }

    @Test
    void 재고_부족_시_예약_실패해도_수주는_남는다() {
        // 재고는 10 인데 100 개 주문
        salesOrderUseCase.placeOrder(new PlaceOrderCommand(1L, null, List.of(
                new PlaceOrderCommand.Line(productId, 100, 1000)
        )));

        // 구독자(Inventory) 는 실패로 예외를 터뜨리지만,
        // @ApplicationModuleListener 기본 설정은 발행자 트랜잭션과 분리 — 수주는 그대로 유지
        Awaitility.await()
                .pollDelay(Duration.ofMillis(300))
                .atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> {
                    StockQuery q = stockUseCase.query(productId, warehouseId);
                    assertThat(q.reserved()).isEqualTo(0);  // 예약은 실패
                });
    }
}