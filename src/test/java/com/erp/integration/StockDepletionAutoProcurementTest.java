package com.erp.integration;

import com.erp.common.domain.Quantity;
import com.erp.common.support.IdGenerator;
import com.erp.inventory.application.dto.command.ReserveStockCommand;
import com.erp.inventory.application.port.inbound.StockUseCase;
import com.erp.inventory.application.port.outbound.StockRepository;
import com.erp.inventory.domain.entity.Stock;
import com.erp.procurement.application.port.outbound.PurchaseOrderRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockDepletionAutoProcurementTest {

    @Autowired private StockUseCase stockUseCase;
    @Autowired private StockRepository stockRepository;
    @Autowired private PurchaseOrderRepository purchaseOrderRepository;

    private final Long productId = 200L;
    private final Long warehouseId = 1L;

    @BeforeEach
    void seed() {
        // 재고 딱 3개만 — 3개 예약하면 가용량 0 → StockDepletedEvent 발생
        Stock stock = Stock.open(productId, warehouseId, Quantity.of(3));
        stock.assignId(IdGenerator.next());
        stockRepository.save(stock);
    }

    @Test
    void 재고_소진_시_Procurement_가_발주() {
        stockUseCase.reserve(new ReserveStockCommand(productId, warehouseId, 3, 999L));

        Awaitility.await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    var orders = purchaseOrderRepository.findByProduct(productId);
                    assertThat(orders).isNotEmpty();
                });
    }
}