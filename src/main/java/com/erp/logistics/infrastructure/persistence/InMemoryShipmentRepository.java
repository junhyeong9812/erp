package com.erp.logistics.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.logistics.application.port.outbound.ShipmentRepository;
import com.erp.logistics.domain.entity.Shipment;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryShipmentRepository extends InMemoryRepository<Shipment, Long> implements ShipmentRepository {
    @Override protected Long extractId(Shipment s) { return s.getId(); }
    @Override public Optional<Shipment> findByOrderId(Long orderId) {
        return findAllBy(s -> s.getOrderId().equals(orderId)).stream().findFirst();
    }
}