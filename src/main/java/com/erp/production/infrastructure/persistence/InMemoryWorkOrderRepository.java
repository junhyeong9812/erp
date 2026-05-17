package com.erp.production.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.production.application.port.outbound.WorkOrderRepository;
import com.erp.production.domain.entity.WorkOrder;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryWorkOrderRepository extends InMemoryRepository<WorkOrder, Long> implements WorkOrderRepository {
    @Override protected Long extractId(WorkOrder w) { return w.getId(); }
}