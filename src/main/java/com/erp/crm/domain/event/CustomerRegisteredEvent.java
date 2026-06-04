package com.erp.crm.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record CustomerRegisteredEvent(Long customerId, String customerCode, String name, Instant occurredAt)
        implements DomainEvent {}