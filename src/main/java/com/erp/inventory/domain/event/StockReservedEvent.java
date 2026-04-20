package com.erp.inventory.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record StockReservedEvent(
        Long stockId,
        Long productId,
        int quantity,
        Instant occurredAt
) implements DomainEvent {}