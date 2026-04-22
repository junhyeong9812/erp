package com.erp.inventory.application.usecase;

import com.erp.common.domain.Quantity;
import com.erp.common.messaging.EventBus;
import com.erp.inventory.application.dto.command.ReceiveStockCommand;
import com.erp.inventory.application.dto.command.ReserveStockCommand;
import com.erp.inventory.application.port.outbound.StockRepository;
import com.erp.inventory.domain.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class StockServiceTest {

    private StockRepository repo;
    private EventBus eventBus;
    private StockService service;

    @BeforeEach
    void setUp() {
        repo = mock(StockRepository.class);
        eventBus = mock(EventBus.class);
        service = new StockService(repo, eventBus);
    }

    @Test
    void 입고_신규_재고_생성() {
        given(repo.findByProductAndWarehouse(1L, 1L)).willReturn(Optional.empty());
        given(repo.save(any(Stock.class))).willAnswer(inv -> inv.getArgument(0));

        service.receive(new ReceiveStockCommand(1L, 1L, 10, "first"));

        then(repo).should().save(any(Stock.class));
    }

    @Test
    void 예약_성공_이벤트_발행() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));
        stock.assignId(100L);
        given(repo.findByProductAndWarehouse(1L, 1L)).willReturn(Optional.of(stock));
        given(repo.save(stock)).willReturn(stock);

        service.reserve(new ReserveStockCommand(1L, 1L, 3, 999L));

        assertThat(stock.reservedQuantity().value()).isEqualTo(3);
        then(eventBus).should().publishAll(argThat(evts -> !evts.isEmpty()));
    }

    @Test
    void 재고_부족_예약_실패() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(2));
        stock.assignId(100L);
        given(repo.findByProductAndWarehouse(1L, 1L)).willReturn(Optional.of(stock));

        assertThatThrownBy(() -> service.reserve(new ReserveStockCommand(1L, 1L, 3, 999L)))
                .isInstanceOf(IllegalStateException.class);
    }
}