package com.erp.sales.application.port.outbound;

import com.erp.sales.domain.entity.SalesOrder;

import java.util.Optional;

public interface SalesOrderRepository {
    SalesOrder save(SalesOrder order);
    Optional<SalesOrder> findById(Long id);
}
