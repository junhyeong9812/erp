package com.erp.logistics.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record ShipmentDispatchedEvent(Long shipmentId, Long orderId, Instant occurredAt) implements DomainEvent {}