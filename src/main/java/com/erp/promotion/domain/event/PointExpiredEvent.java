package com.erp.promotion.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record PointExpiredEvent(Long pointId, Long customerId, int amount, Instant occurredAt)
        implements DomainEvent {}