package com.erp.hr.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeHiredEvent(Long employeeId, String employeeNumber, String name,
                                 LocalDate hiredAt, Instant occurredAt) implements DomainEvent {}