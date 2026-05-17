package com.erp.production.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record WorkOrderIssuedEvent(Long workOrderId, Long productId, int quantity, Instant occurredAt)
        implements DomainEvent {}