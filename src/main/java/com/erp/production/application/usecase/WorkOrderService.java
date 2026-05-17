package com.erp.production.application.usecase;

import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.production.application.dto.command.IssueWorkOrderCommand;
import com.erp.production.application.port.inbound.WorkOrderUseCase;
import com.erp.production.application.port.outbound.WorkOrderRepository;
import com.erp.production.domain.entity.WorkOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkOrderService implements WorkOrderUseCase {

    private final WorkOrderRepository workOrderRepository;
    private final EventBus eventBus;

    public WorkOrderService(WorkOrderRepository workOrderRepository, EventBus eventBus) {
        this.workOrderRepository = workOrderRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Long issueWorkOrder(IssueWorkOrderCommand cmd) {
        WorkOrder wo = WorkOrder.issue(cmd.productId(), cmd.plannedQuantity());
        wo.assignId(IdGenerator.next());
        workOrderRepository.save(wo);
        eventBus.publishAll(wo.pullEvents());
        return wo.getId();
    }

    @Override
    public void recordProduction(Long workOrderId, int produced, int defective) {
        WorkOrder wo = workOrderRepository.findById(workOrderId).orElseThrow(NotFoundException::new);
        wo.recordProduction(produced, defective);
        workOrderRepository.save(wo);
        eventBus.publishAll(wo.pullEvents());
    }
}