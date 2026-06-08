package com.erp.notification.application.port.outbound;

import com.erp.notification.domain.entity.Notification;

import java.util.Optional;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(Long id);
}