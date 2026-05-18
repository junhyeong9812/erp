package com.erp.production.application.usecase;

import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.production.application.dto.command.IssueWorkOrderCommand;
import com.erp.production.application.port.outbound.WorkOrderRepository;
import com.erp.production.domain.entity.WorkOrder;
import com.erp.production.domain.event.ProductionCompletedEvent;
import com.erp.production.domain.event.WorkOrderIssuedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class WorkOrderServiceTest {

    WorkOrderRepository workOrderRepository;
    EventBus eventBus;
    WorkOrderService service;

    @BeforeEach
    void setUp() {
        workOrderRepository = mock(WorkOrderRepository.class);
        eventBus = mock(EventBus.class);
        when(workOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service = new WorkOrderService(workOrderRepository, eventBus);
    }

    @Test
    void issueWorkOrder_는_WorkOrder_저장_후_Issued_이벤트_발행() {
        Long id = service.issueWorkOrder(new IssueWorkOrderCommand(100L, 50));

        ArgumentCaptor<WorkOrder> captor = ArgumentCaptor.forClass(WorkOrder.class);
        verify(workOrderRepository).save(captor.capture());
        WorkOrder saved = captor.getValue();

        assertThat(id).isNotNull();
        assertThat(saved.getProductId()).isEqualTo(100L);
        assertThat(saved.getPlannedQuantity()).isEqualTo(50);
        assertThat(saved.getStatus()).isEqualTo(WorkOrder.Status.PLANNED);

        // pullEvents 후 이벤트 발행
        verify(eventBus).publishAll(argThat(events ->
                events.stream().anyMatch(e -> e instanceof WorkOrderIssuedEvent)));
        // 저장된 WorkOrder 의 events 는 pullEvents 로 비워진 상태여야 함
        assertThat(saved.events()).isEmpty();
    }

    @Test
    void recordProduction_계획_도달_시_Completed_이벤트_발행() {
        WorkOrder wo = WorkOrder.issue(100L, 50);
        wo.assignId(1L);
        wo.pullEvents();  // Issued 이벤트는 이미 issueWorkOrder 시 발행됐다고 가정
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(wo));

        service.recordProduction(1L, 50, 0);

        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.COMPLETED);
        verify(workOrderRepository).save(wo);
        verify(eventBus).publishAll(argThat(events ->
                events.stream().anyMatch(e -> e instanceof ProductionCompletedEvent)));
    }

    @Test
    void recordProduction_계획_미달이면_IN_PROGRESS_Completed_이벤트_없음() {
        WorkOrder wo = WorkOrder.issue(100L, 50);
        wo.assignId(1L);
        wo.pullEvents();
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(wo));

        service.recordProduction(1L, 20, 0);

        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.IN_PROGRESS);
        verify(eventBus).publishAll(argThat(events ->
                events.stream().noneMatch(e -> e instanceof ProductionCompletedEvent)));
    }

    @Test
    void recordProduction_대상_WorkOrder_없으면_NotFoundException() {
        when(workOrderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recordProduction(404L, 10, 0))
                .isInstanceOf(NotFoundException.class);

        verify(workOrderRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }
}