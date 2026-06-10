package com.erp.report.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;

public record ReportGeneratedEvent(Long reportId, String reportType, LocalDate targetDate, Instant occurredAt)
        implements DomainEvent {}