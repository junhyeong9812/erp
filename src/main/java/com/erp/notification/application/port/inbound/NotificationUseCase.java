package com.erp.notification.application.port.inbound;

import com.erp.notification.application.dto.command.SendNotificationCommand;

public interface NotificationUseCase {
    Long send(SendNotificationCommand command);
}