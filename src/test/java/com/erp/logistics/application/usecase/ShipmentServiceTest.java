package com.erp.logistics.application.usecase;

import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.logistics.application.dto.command.DispatchShipmentCommand;
import com.erp.logistics.application.port.outbound.DeliveryRepository;
import com.erp.logistics.application.port.outbound.ShipmentRepository;
import com.erp.logistics.domain.entity.Delivery;
import com.erp.logistics.domain.entity.Shipment;
import com.erp.logistics.domain.event.ShipmentDispatchedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShipmentServiceTest {

    ShipmentRepository shipmentRepository;
    DeliveryRepository deliveryRepository;
    EventBus eventBus;
    ShipmentService service;

    @BeforeEach
    void setUp() {
        shipmentRepository = mock(ShipmentRepository.class);
        deliveryRepository = mock(DeliveryRepository.class);
        eventBus = mock(EventBus.class);
        when(shipmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(deliveryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service = new ShipmentService(shipmentRepository, deliveryRepository, eventBus);
    }

    @Test
    void createShipmentForOrder_는_PREPARING_Shipment_를_저장하고_id_반환() {
        Long id = service.createShipmentForOrder(1L, 10L);

        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository).save(captor.capture());
        Shipment saved = captor.getValue();

        assertThat(id).isNotNull();
        assertThat(saved.getOrderId()).isEqualTo(1L);
        assertThat(saved.getStatus()).isEqualTo(Shipment.Status.PREPARING);
    }

    @Test
    void dispatch_는_Shipment_dispatch_후_이벤트_발행하고_Delivery_생성() {
        Shipment s = Shipment.instruct(1L, 10L);
        s.assignId(100L);
        when(shipmentRepository.findById(100L)).thenReturn(Optional.of(s));

        service.dispatch(new DispatchShipmentCommand(100L, "driver-1", "TRK-001"));

        // Shipment 가 DISPATCHED 로 전이되고 저장됨
        assertThat(s.getStatus()).isEqualTo(Shipment.Status.DISPATCHED);
        verify(shipmentRepository).save(s);

        // pullEvents 후 이벤트 발행 (ShipmentDispatchedEvent)
        verify(eventBus).publishAll(argThat(events ->
                events.stream().anyMatch(e -> e instanceof ShipmentDispatchedEvent)));

        // Delivery 저장 확인
        ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());
        Delivery d = deliveryCaptor.getValue();
        assertThat(d.getShipmentId()).isEqualTo(100L);
        assertThat(d.getStatus()).isEqualTo(Delivery.Status.ASSIGNED);
    }

    @Test
    void dispatch_대상_Shipment_없으면_NotFoundException() {
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.dispatch(new DispatchShipmentCommand(999L, "drv", "T")))
                .isInstanceOf(NotFoundException.class);

        verify(deliveryRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }

    @Test
    void completeDelivery_는_Delivery_deliver_후_이벤트_발행() {
        Delivery d = Delivery.assign(100L, "driver-1", "TRK-001");
        d.assignId(500L);
        when(deliveryRepository.findById(500L)).thenReturn(Optional.of(d));

        service.completeDelivery(500L);

        assertThat(d.getStatus()).isEqualTo(Delivery.Status.DELIVERED);
        verify(deliveryRepository).save(d);
        verify(eventBus).publishAll(argThat(events -> !events.isEmpty()));
    }

    @Test
    void completeDelivery_대상_없으면_NotFoundException() {
        when(deliveryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.completeDelivery(404L))
                .isInstanceOf(NotFoundException.class);
    }
}