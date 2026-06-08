package com.erp.notification.application.usecase;

import com.erp.approval.domain.event.ApprovalRequestedEvent;
import com.erp.notification.application.dto.command.SendNotificationCommand;
import com.erp.notification.application.port.inbound.NotificationUseCase;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class GlobalEventHandler {

    private final NotificationUseCase notificationUseCase;

    public GlobalEventHandler(NotificationUseCase notificationUseCase) {
        this.notificationUseCase = notificationUseCase;
    }

    @ApplicationModuleListener
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        notificationUseCase.send(new SendNotificationCommand(
                event.orderId(),
                "결제 완료",
                "결제가 완료되었습니다. 금액: " + event.amount(),
                "EMAIL"));
    }

    @ApplicationModuleListener
    public void onApprovalRequested(ApprovalRequestedEvent event) {
        notificationUseCase.send(new SendNotificationCommand(
                event.drafterId(),
                "결재 요청",
                event.documentType() + " 결재가 요청되었습니다.",
                "SYSTEM"));
    }
}