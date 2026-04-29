package com.erp.settlement.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.settlement.application.port.outbound.SellerSettlementRepository;
import com.erp.settlement.domain.entity.SellerSettlement;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemorySellerSettlementRepository
        extends InMemoryRepository<SellerSettlement, Long>
        implements SellerSettlementRepository {

    @Override
    protected Long extractId(SellerSettlement entity) {
        return null;
    }

    @Override
    public Optional<SellerSettlement> findBySellerAndPeriod(Long sellerId, Long periodId) {
        return Optional.empty();
    }
}
