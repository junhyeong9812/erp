package com.erp.settlement.application.port.inbound;

public interface SellerSettlementUseCase {
    Long calculate(Long sellerId, Long periodId);

    void markPaid(Long settlementId);
}
