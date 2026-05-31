package com.erp.approval.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record ApprovalRequestedEvent(Long documentId, Long drafterId, String documentType, Instant occurredAt)
        implements DomainEvent {}