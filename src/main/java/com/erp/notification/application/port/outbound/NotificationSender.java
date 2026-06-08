package com.erp.notification.application.port.outbound;

import com.erp.notification.domain.entity.Notification;

public interface NotificationSender {
    boolean deliver(Notification notification);
}