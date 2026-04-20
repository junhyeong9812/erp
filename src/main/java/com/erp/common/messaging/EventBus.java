package com.erp.common.messaging;

import com.erp.common.domain.DomainEvent;

import java.util.List;

public interface EventBus {
    void publish(DomainEvent event);

    default void publishAll(List<? extends DomainEvent> events) {
        events.forEach(this::publish);
    }
}