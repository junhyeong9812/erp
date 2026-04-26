package com.erp.payment.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.payment.domain.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrderRepository extends InMemoryRepository<Order, Long> {
    @Override
    protected Long extractId(Order entity) { return entity.getId(); }
}