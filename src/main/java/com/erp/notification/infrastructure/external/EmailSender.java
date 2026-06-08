package com.erp.notification.infrastructure.external;

import com.erp.notification.application.port.outbound.NotificationSender;
import com.erp.notification.domain.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class EmailSender implements NotificationSender {

    @Override
    public boolean deliver(Notification n) {
        if (n.getChannel() != Notification.Channel.EMAIL) return false;
        System.out.println("[EMAIL] to=" + n.getRecipientId() + " title=" + n.getTitle());
        return true;
    }
}