package com.erp.logistics.application.port.outbound;

import com.erp.logistics.domain.entity.Delivery;
import com.erp.logistics.domain.entity.Shipment;

import java.util.Optional;

public interface ShipmentRepository {
    Shipment save(Shipment shipment);
    Optional<Shipment> findById(Long id);
    Optional<Shipment> findByOrderId(Long orderId);
}