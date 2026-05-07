package com.erp.logistics.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record DeliveryCompletedEvent(Long deliveryId, Long shipmentId, Instant occurredAt) implements DomainEvent {}