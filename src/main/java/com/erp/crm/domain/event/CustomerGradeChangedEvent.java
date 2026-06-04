package com.erp.crm.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record CustomerGradeChangedEvent(Long customerId, String oldGrade, String newGrade, Instant occurredAt)
        implements DomainEvent {}