package com.erp.notification.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record NotificationSentEvent(Long notificationId, Long recipientId, String channel, Instant occurredAt)
        implements DomainEvent {}