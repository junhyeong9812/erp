package com.erp.logistics.application.port.outbound;

import com.erp.logistics.domain.entity.Delivery;
import com.erp.logistics.domain.entity.Shipment;

import java.util.Optional;

public interface DeliveryRepository {
    Delivery save(Delivery delivery);
    Optional<Delivery> findById(Long id);
}