package com.erp.procurement.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record GoodsReceivedEvent(Long purchaseOrderId, Long productId, int quantity, Instant occurredAt)
        implements DomainEvent {}