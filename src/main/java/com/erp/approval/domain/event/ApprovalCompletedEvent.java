package com.erp.approval.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record ApprovalCompletedEvent(Long documentId, String documentType, boolean approved, Instant occurredAt)
        implements DomainEvent {}