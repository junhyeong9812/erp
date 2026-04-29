package com.erp.settlement.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.domain.entity.SettlementPeriod;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemorySettlementPeriodRepository
        extends InMemoryRepository<SettlementPeriod, Long>
        implements SettlementPeriodRepository {

    @Override
    protected Long extractId(SettlementPeriod entity) {
        return null;
    }

    @Override
    public Optional<SettlementPeriod> findOpenContaining(LocalDate date) {
        return Optional.empty();
    }

    @Override
    public List<SettlementPeriod> findOpenEndingBefore(LocalDate date) {
        return List.of();
    }
}
