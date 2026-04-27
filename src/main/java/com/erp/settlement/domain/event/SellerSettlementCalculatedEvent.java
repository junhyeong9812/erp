package com.erp.settlement.domain.event;

import com.erp.common.domain.DomainEvent;
import java.time.Instant;

public record SellerSettlementCalculatedEvent(Long settlementId, Long sellerId,
                                              Long periodId, long netPayout,
                                              Instant occurredAt) implements DomainEvent {}