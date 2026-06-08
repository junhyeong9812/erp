package com.erp.notification.infrastructure.external;

import com.erp.notification.application.port.outbound.NotificationSender;
import com.erp.notification.domain.entity.Notification;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
public class CompositeSender implements NotificationSender {

    private final List<NotificationSender> senders;

    public CompositeSender(List<NotificationSender> senders) {
        this.senders = senders.stream()
                .filter(s -> !(s instanceof CompositeSender))
                .toList();
    }

    @Override
    public boolean deliver(Notification n) {
        for (NotificationSender s : senders) {
            if (s.deliver(n)) return true;
        }
        return false;
    }
}