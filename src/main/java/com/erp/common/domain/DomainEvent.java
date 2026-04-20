package com.erp.common.domain;

import java.time.Instant;

public interface DomainEvent {
    default Instant occurredAt() {
        return Instant.now();
    }
}