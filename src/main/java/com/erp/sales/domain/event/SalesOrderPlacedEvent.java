package com.erp.sales.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;
import java.util.List;

public record SalesOrderPlacedEvent(
        Long orderId,
        Long customerId,
        List<Line> lines,
        Instant occurredAt
) implements DomainEvent {
    public record Line(Long productId, int quantity) {}
}