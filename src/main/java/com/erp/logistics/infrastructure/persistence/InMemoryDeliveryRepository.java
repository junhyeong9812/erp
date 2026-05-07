package com.erp.logistics.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.logistics.application.port.outbound.DeliveryRepository;
import com.erp.logistics.domain.entity.Delivery;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDeliveryRepository extends InMemoryRepository<Delivery, Long> implements DeliveryRepository {
    @Override protected Long extractId(Delivery d) { return d.getId(); }
}