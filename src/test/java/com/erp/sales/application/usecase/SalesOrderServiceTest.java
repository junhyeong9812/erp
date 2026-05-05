package com.erp.sales.application.usecase;

import com.erp.common.messaging.EventBus;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.port.outbound.SalesOrderRepository;
import com.erp.sales.domain.entity.SalesOrder;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock SalesOrderRepository orderRepository;
    @Mock EventBus eventBus;
    @InjectMocks SalesOrderService service;

    @Test
    void placeOrder_는_저장_후_SalesOrderPlacedEvent_발행() {
        when(orderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        PlaceOrderCommand cmd = new PlaceOrderCommand(
                1L, 10L,
                List.of(
                        new PlaceOrderCommand.Line(100L, 2, 1000L),
                        new PlaceOrderCommand.Line(200L, 1, 3000L)
                ));

        Long id = service.placeOrder(cmd);

        assertThat(id).isNotNull();

        ArgumentCaptor<SalesOrder> savedCap = ArgumentCaptor.forClass(SalesOrder.class);
        verify(orderRepository).save(savedCap.capture());
        SalesOrder saved = savedCap.getValue();
        assertThat(saved.getCustomerId()).isEqualTo(1L);
        assertThat(saved.getTotalAmount().amount().longValueExact()).isEqualTo(5000L);

        ArgumentCaptor<List<? extends com.erp.common.domain.DomainEvent>> eventCap =
                ArgumentCaptor.forClass(List.class);
        verify(eventBus).publishAll(eventCap.capture());
        assertThat(eventCap.getValue()).hasAtLeastOneElementOfType(SalesOrderPlacedEvent.class);
    }

    @Test
    void placeOrder_빈_라인이면_총액_0_으로_저장() {
        when(orderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        PlaceOrderCommand cmd = new PlaceOrderCommand(1L, 10L, List.of());

        service.placeOrder(cmd);

        ArgumentCaptor<SalesOrder> cap = ArgumentCaptor.forClass(SalesOrder.class);
        verify(orderRepository).save(cap.capture());
        assertThat(cap.getValue().getTotalAmount().amount().longValueExact()).isZero();
    }
}