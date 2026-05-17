package com.erp.production.application.port.outbound;

import com.erp.production.domain.entity.BillOfMaterials;
import com.erp.production.domain.entity.WorkOrder;

import java.util.Optional;

public interface WorkOrderRepository {
    WorkOrder save(WorkOrder order);
    Optional<WorkOrder> findById(Long id);
}