package com.erp.settlement.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;
import java.util.List;

public record LedgerUnbalancedEvent(Long periodId, List<Long> unbalancedLedgerIds,
                                    Instant occurredAt) implements DomainEvent {}