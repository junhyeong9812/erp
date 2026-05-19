package com.erp.hr.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;

public record PayrollCalculatedEvent(Long payrollId, Long employeeId, String period,
                                     long netSalary, Instant occurredAt) implements DomainEvent {}