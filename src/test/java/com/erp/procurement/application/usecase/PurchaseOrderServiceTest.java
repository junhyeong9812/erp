package com.erp.procurement.application.usecase;

import com.erp.common.domain.DomainEvent;
import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.procurement.application.dto.command.IssuePurchaseOrderCommand;
import com.erp.procurement.application.dto.command.ReceiveGoodsCommand;
import com.erp.procurement.application.port.outbound.PurchaseOrderRepository;
import com.erp.procurement.domain.entity.PurchaseOrder;
import com.erp.procurement.domain.event.GoodsReceivedEvent;
import com.erp.procurement.domain.event.PurchaseOrderIssuedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock PurchaseOrderRepository repository;
    @Mock EventBus eventBus;
    @InjectMocks PurchaseOrderService service;

    @Test
    void issuePurchaseOrder_는_저장_후_PurchaseOrderIssuedEvent_발행() {
        when(repository.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        Long id = service.issuePurchaseOrder(
                new IssuePurchaseOrderCommand(1L, 100L, 50, 1000L));

        assertThat(id).isNotNull();

        ArgumentCaptor<PurchaseOrder> saved = ArgumentCaptor.forClass(PurchaseOrder.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getSupplierId()).isEqualTo(1L);
        assertThat(saved.getValue().getProductId()).isEqualTo(100L);
        assertThat(saved.getValue().getQuantity()).isEqualTo(50);
        assertThat(saved.getValue().getUnitPrice()).isEqualTo(Money.of(1000));

        ArgumentCaptor<List<? extends DomainEvent>> events = ArgumentCaptor.forClass(List.class);
        verify(eventBus).publishAll(events.capture());
        assertThat(events.getValue()).hasAtLeastOneElementOfType(PurchaseOrderIssuedEvent.class);
    }

    @Test
    void receiveGoods_는_도메인_receive_호출_후_GoodsReceivedEvent_발행() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po.assignId(10L);
        po.pullEvents();   // 이전 이벤트 비움

        when(repository.findById(10L)).thenReturn(Optional.of(po));
        when(repository.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        service.receiveGoods(new ReceiveGoodsCommand(10L, 20));

        assertThat(po.getReceivedQuantity()).isEqualTo(20);
        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.PARTIAL);

        verify(repository).save(po);
        ArgumentCaptor<List<? extends DomainEvent>> cap = ArgumentCaptor.forClass(List.class);
        verify(eventBus).publishAll(cap.capture());
        assertThat(cap.getValue()).hasAtLeastOneElementOfType(GoodsReceivedEvent.class);
    }

    @Test
    void receiveGoods_없는_PO_면_NotFoundException_이벤트_발행_안함() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.receiveGoods(new ReceiveGoodsCommand(999L, 5)))
                .isInstanceOf(NotFoundException.class);

        verify(eventBus, never()).publishAll(anyList());
        verify(repository, never()).save(any());
    }
}