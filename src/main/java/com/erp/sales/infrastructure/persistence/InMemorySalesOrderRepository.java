package com.erp.sales.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.sales.domain.entity.SalesOrder;
import org.springframework.stereotype.Repository;

@Repository
public class InMemorySalesOrderRepository extends InMemoryRepository<SalesOrder, Long>
        implements com.erp.sales.application.port.outbound.SalesOrderRepository {
    @Override
    protected Long extractId(SalesOrder o) {
        return o.getId();
    }
}
