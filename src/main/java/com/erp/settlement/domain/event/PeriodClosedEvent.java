package com.erp.settlement.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;

public record PeriodClosedEvent(Long periodId, LocalDate startDate, LocalDate endDate,
                                Instant occurredAt) implements DomainEvent {}