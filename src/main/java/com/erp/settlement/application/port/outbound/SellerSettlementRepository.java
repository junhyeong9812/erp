package com.erp.settlement.application.port.outbound;

import com.erp.settlement.domain.entity.SellerSettlement;

import java.util.Optional;

public interface SellerSettlementRepository {
    SellerSettlement save(SellerSettlement s);

    Optional<SellerSettlement> findById(Long id);

    Optional<SellerSettlement> findBySellerAndPeriod(Long sellerId, Long periodId);
}
