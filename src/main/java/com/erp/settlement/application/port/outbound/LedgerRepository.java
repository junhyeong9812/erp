package com.erp.settlement.application.port.outbound;

import com.erp.settlement.domain.entity.Ledger;

import java.util.List;
import java.util.Optional;

public interface LedgerRepository {
    Ledger save(Ledger ledger);
    Optional<Ledger> findById(Long id);
    List<Ledger> findByPeriodId(Long periodId);
    List<Ledger> findByPeriodAndType(Long periodId, Ledger.Type type);
    List<Ledger> findBySellerInPeriod(Long sellerId, Long periodId);   // 판매자 정산용 (description/reference 기반 매핑)
    boolean existsReversalOf(Long originalLedgerId);
}