package com.erp.sales.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.ConflictException;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.port.outbound.QuoteRepository;
import com.erp.sales.application.port.outbound.SalesOrderRepository;
import com.erp.sales.domain.entity.Quote;
import com.erp.sales.domain.entity.SalesOrder;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock SalesOrderRepository orderRepository;
    @Mock QuoteRepository quoteRepository;
    @Mock EventBus eventBus;
    @InjectMocks SalesOrderService service;

    @Test
    void placeOrder_는_저장_후_SalesOrderPlacedEvent_발행() {
        when(orderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        PlaceOrderCommand cmd = new PlaceOrderCommand(
                1L, null,
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
        SalesOrderPlacedEvent ev = (SalesOrderPlacedEvent) eventCap.getValue().get(0);
        assertThat(ev.orderId()).as("이벤트 orderId 는 assignId 후의 실제 ID 여야 한다").isEqualTo(id);
        assertThat(ev.customerId()).isEqualTo(1L);
    }

    @Test
    void placeOrder_빈_라인이면_총액_0_으로_저장() {
        when(orderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        PlaceOrderCommand cmd = new PlaceOrderCommand(1L, null, List.of());

        service.placeOrder(cmd);

        ArgumentCaptor<SalesOrder> cap = ArgumentCaptor.forClass(SalesOrder.class);
        verify(orderRepository).save(cap.capture());
        assertThat(cap.getValue().getTotalAmount().amount().longValueExact()).isZero();
    }

    @Test
    void placeOrder_quoteId_지정시_ACTIVE_견적은_자동_수락() {
        when(orderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        when(quoteRepository.save(any(Quote.class))).thenAnswer(inv -> inv.getArgument(0));

        Quote active = Quote.issue(1L, Money.of(1000), LocalDate.of(2030, 1, 1));
        active.assignId(77L);
        when(quoteRepository.findById(77L)).thenReturn(Optional.of(active));

        service.placeOrder(new PlaceOrderCommand(
                1L, 77L,
                List.of(new PlaceOrderCommand.Line(100L, 1, 1000L))));

        assertThat(active.getStatus()).isEqualTo(Quote.Status.ACCEPTED);
        verify(quoteRepository).save(active);
    }

    @Test
    void placeOrder_만료된_견적이면_ConflictException() {
        Quote expired = Quote.issue(1L, Money.of(1000), LocalDate.of(2020, 1, 1));
        expired.assignId(88L);
        expired.expire();
        when(quoteRepository.findById(88L)).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.placeOrder(new PlaceOrderCommand(
                1L, 88L,
                List.of(new PlaceOrderCommand.Line(100L, 1, 1000L)))))
                .isInstanceOf(ConflictException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void placeOrder_없는_견적_참조시_NotFoundException() {
        when(quoteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.placeOrder(new PlaceOrderCommand(
                1L, 999L,
                List.of(new PlaceOrderCommand.Line(100L, 1, 1000L)))))
                .isInstanceOf(NotFoundException.class);

        verify(orderRepository, never()).save(any());
    }
}
