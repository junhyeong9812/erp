package com.erp.sales.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record QuoteExpiredEvent(Long quoteId, Long customerId, Instant occurredAt) implements DomainEvent {}