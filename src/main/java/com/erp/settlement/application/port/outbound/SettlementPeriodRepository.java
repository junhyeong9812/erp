package com.erp.settlement.application.port.outbound;

import com.erp.settlement.domain.entity.SettlementPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SettlementPeriodRepository {
    SettlementPeriod save(SettlementPeriod period);
    Optional<SettlementPeriod> findById(Long id);
    Optional<SettlementPeriod> findOpenContaining(LocalDate date);
    List<SettlementPeriod> findOpenEndingBefore(LocalDate date);
}

