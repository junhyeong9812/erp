package com.erp.logistics.application.usecase;

import com.erp.logistics.application.port.inbound.ShipmentUseCase;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedEventHandler {

    private final ShipmentUseCase shipmentUseCase;

    public PaymentCompletedEventHandler(ShipmentUseCase shipmentUseCase) {
        this.shipmentUseCase = shipmentUseCase;
    }

    @ApplicationModuleListener
    public void on(PaymentCompletedEvent event) {
        shipmentUseCase.createShipmentForOrder(event.orderId(), 1L);  // 기본 창고
    }
}