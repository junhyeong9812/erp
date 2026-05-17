package com.erp.production.application.port.inbound;

import com.erp.production.application.dto.command.IssueWorkOrderCommand;

public interface WorkOrderUseCase {
    Long issueWorkOrder(IssueWorkOrderCommand command);
    void recordProduction(Long workOrderId, int produced, int defective);
}