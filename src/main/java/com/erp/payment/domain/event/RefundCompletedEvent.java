package com.erp.payment.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record RefundCompletedEvent(
        Long refundId,
        Long paymentId,
        Long orderId,
        long amount,
        Instant occurredAt
) implements DomainEvent {}