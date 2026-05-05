package com.erp.settlement.domain.event;

import com.erp.common.domain.DomainEvent;

import java.time.Instant;

public record OverdueInvoiceEvent(Long invoiceId, long overdueDays, Instant occurredAt)
        implements DomainEvent {}
