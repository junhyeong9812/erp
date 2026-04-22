package com.erp.payment.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record PaymentCompletedEvent(
        Long paymentId,
        Long orderId,
        long amount,
        Instant occurredAt
) implements DomainEvent {}