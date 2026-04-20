package com.erp.inventory.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record StockDepletedEvent(
        Long stockId,
        Long productId,
        Instant occurredAt
) implements DomainEvent {}