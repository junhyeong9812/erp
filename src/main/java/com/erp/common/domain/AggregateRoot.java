package com.erp.common.domain;

import jakarta.persistence.MappedSuperclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@MappedSuperclass
public abstract class AggregateRoot extends BaseEntity {

    private final transient List<DomainEvent> events = new ArrayList<>();

    protected void register(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> copy = List.copyOf(events);
        events.clear();
        return copy;
    }

    public List<DomainEvent> events() {
        return Collections.unmodifiableList(events);
    }
}