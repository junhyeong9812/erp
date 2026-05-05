package com.erp.procurement.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record PurchaseOrderIssuedEvent(
        Long purchaseOrderId, Long supplierId, Long productId, int quantity, Instant occurredAt
) implements DomainEvent {}