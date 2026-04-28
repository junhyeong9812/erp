package com.erp.settlement.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.domain.entity.Ledger;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryLedgerRepository
        extends InMemoryRepository<Ledger, Long>
        implements LedgerRepository {

    @Override
    protected Long extractId(Ledger entity) { return entity.getId(); }

    @Override
    public List<Ledger> findByPeriodId(Long periodId) {
        return findAllBy(l -> periodId.equals(l.getPeriodId()));
    }

    @Override
    public List<Ledger> findByPeriodAndType(Long periodId, Ledger.Type type) {
        return findAllBy(l -> periodId.equals(l.getPeriodId()) && l.getType() == type);
    }

    @Override
    public List<Ledger> findBySellerInPeriod(Long sellerId, Long periodId) {
        // 컨벤션: description 에 "seller=<id>" 토큰을 포함시켜 매핑한다.
        // ※ 이 메서드를 쓰려면 Ledger 엔티티에 getDescription() 게터가 필요하다.
        //   (현재 도메인 엔티티에는 description 필드는 있지만 getter 가 없음 — 별도 정정 문서 참조)
        String tag = "seller=" + sellerId;
        return findAllBy(l -> periodId.equals(l.getPeriodId())
                && l.getDescription() != null
                && l.getDescription().contains(tag));
    }

    @Override
    public boolean existsReversalOf(Long originalLedgerId) {
        return !findAllBy(l ->
                l.getType() == Ledger.Type.REVERSAL
                        && "LEDGER".equals(l.getReferenceType())
                        && originalLedgerId.equals(l.getReferenceId())
        ).isEmpty();
    }
}