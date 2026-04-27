package com.erp.settlement.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record LedgerCreatedEvent(Long ledgerId, String type, long amount, Instant occurredAt)
        implements DomainEvent {}