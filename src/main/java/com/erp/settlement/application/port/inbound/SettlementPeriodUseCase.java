package com.erp.settlement.application.port.inbound;

import java.time.LocalDate;
import java.util.Optional;

public interface SettlementPeriodUseCase {
    Long open(LocalDate start, LocalDate end);
    /** 기간 마감 — 내부에서 LedgerReconciliation.verify 선행. 실패 시 ConflictException. */
    void close(Long periodId);
    Optional<Long> currentOpenPeriodId(LocalDate at);
}

