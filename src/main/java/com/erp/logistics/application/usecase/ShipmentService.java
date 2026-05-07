package com.erp.logistics.application.usecase;

import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.logistics.application.dto.command.DispatchShipmentCommand;
import com.erp.logistics.application.port.inbound.ShipmentUseCase;
import com.erp.logistics.application.port.outbound.DeliveryRepository;
import com.erp.logistics.application.port.outbound.ShipmentRepository;
import com.erp.logistics.domain.entity.Delivery;
import com.erp.logistics.domain.entity.Shipment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ShipmentService implements ShipmentUseCase {

    private final ShipmentRepository shipmentRepository;
    private final DeliveryRepository deliveryRepository;
    private final EventBus eventBus;

    public ShipmentService(ShipmentRepository shipmentRepository,
                           DeliveryRepository deliveryRepository,
                           EventBus eventBus) {
        this.shipmentRepository = shipmentRepository;
        this.deliveryRepository = deliveryRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Long createShipmentForOrder(Long orderId, Long warehouseId) {
        Shipment s = Shipment.instruct(orderId, warehouseId);
        s.assignId(IdGenerator.next());
        shipmentRepository.save(s);
        return s.getId();
    }

    @Override
    public void dispatch(DispatchShipmentCommand cmd) {
        Shipment s = shipmentRepository.findById(cmd.shipmentId()).orElseThrow(NotFoundException::new);
        s.dispatch();
        shipmentRepository.save(s);
        eventBus.publishAll(s.pullEvents());

        Delivery d = Delivery.assign(s.getId(), cmd.driverId(), cmd.trackingNumber());
        d.assignId(IdGenerator.next());
        deliveryRepository.save(d);
    }

    @Override
    public void completeDelivery(Long deliveryId) {
        Delivery d = deliveryRepository.findById(deliveryId).orElseThrow(NotFoundException::new);
        d.deliver();
        deliveryRepository.save(d);
        eventBus.publishAll(d.pullEvents());
    }
}