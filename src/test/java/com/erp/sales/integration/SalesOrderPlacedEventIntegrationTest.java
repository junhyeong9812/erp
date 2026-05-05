package com.erp.sales.integration;

import com.erp.common.messaging.SpringEventBus;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.port.outbound.QuoteRepository;
import com.erp.sales.application.port.outbound.SalesOrderRepository;
import com.erp.sales.application.usecase.SalesOrderService;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import com.erp.sales.infrastructure.persistence.InMemoryQuoteRepository;
import com.erp.sales.infrastructure.persistence.InMemorySalesOrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        SpringEventBus.class,
        SalesOrderPlacedEventIntegrationTest.TestConfig.class,
        SalesOrderPlacedEventIntegrationTest.InventoryReservationListener.class
})
class SalesOrderPlacedEventIntegrationTest {

    @Configuration
    static class TestConfig {
        @Bean SalesOrderRepository salesOrderRepository() { return new InMemorySalesOrderRepository(); }
        @Bean QuoteRepository quoteRepository() { return new InMemoryQuoteRepository(); }
        @Bean SalesOrderService salesOrderService(SalesOrderRepository orderRepo,
                                                  QuoteRepository quoteRepo,
                                                  com.erp.common.messaging.EventBus bus) {
            return new SalesOrderService(orderRepo, quoteRepo, bus);
        }
    }

    @Component
    static class InventoryReservationListener {
        final List<SalesOrderPlacedEvent> received = new ArrayList<>();
        @EventListener
        public void on(SalesOrderPlacedEvent e) { received.add(e); }
    }

    @Autowired SalesOrderService salesOrderService;
    @Autowired InventoryReservationListener listener;

    @Test
    void placeOrder_호출_시_SalesOrderPlacedEvent_가_리스너에_도달() {
        listener.received.clear();

        Long orderId = salesOrderService.placeOrder(new PlaceOrderCommand(
                1L, null,
                List.of(new PlaceOrderCommand.Line(100L, 2, 1000L))));

        assertThat(listener.received).hasSize(1);
        SalesOrderPlacedEvent e = listener.received.get(0);
        assertThat(e.orderId()).as("이벤트 orderId 는 발행 시점의 실제 ID여야 한다").isEqualTo(orderId);
        assertThat(e.customerId()).isEqualTo(1L);
        assertThat(e.lines()).extracting(SalesOrderPlacedEvent.Line::productId)
                .containsExactly(100L);
    }
}
