package com.erp.notification.infrastructure.external;

import com.erp.notification.application.port.outbound.NotificationSender;
import com.erp.notification.domain.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class SmsSender implements NotificationSender {

    @Override
    public boolean deliver(Notification n) {
        if (n.getChannel() != Notification.Channel.SMS) return false;
        System.out.println("[SMS] to=" + n.getRecipientId() + " msg=" + n.getBody());
        return true;
    }
}