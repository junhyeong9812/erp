package com.erp.logistics.application.port.inbound;

import com.erp.logistics.application.dto.command.DispatchShipmentCommand;

public interface ShipmentUseCase {
    Long createShipmentForOrder(Long orderId, Long warehouseId);
    void dispatch(DispatchShipmentCommand command);
    void completeDelivery(Long deliveryId);
}