package com.erp.notification.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.notification.application.port.outbound.NotificationRepository;
import com.erp.notification.domain.entity.Notification;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryNotificationRepository extends InMemoryRepository<Notification, Long>
        implements NotificationRepository {
    @Override protected Long extractId(Notification n) { return n.getId(); }
}