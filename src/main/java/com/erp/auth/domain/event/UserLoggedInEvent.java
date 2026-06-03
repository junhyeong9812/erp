package com.erp.auth.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record UserLoggedInEvent(Long userId, String username, Instant occurredAt) implements DomainEvent {}