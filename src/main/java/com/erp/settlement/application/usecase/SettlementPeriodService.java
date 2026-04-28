package com.erp.settlement.application.usecase;

import com.erp.common.exception.ConflictException;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.settlement.domain.exception.SettlementErrorCode;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.domain.entity.SettlementPeriod;
import com.erp.settlement.domain.event.LedgerUnbalancedEvent;
import com.erp.settlement.domain.event.PeriodClosedEvent;
import com.erp.settlement.domain.service.LedgerReconciliation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class SettlementPeriodService implements SettlementPeriodUseCase {

    private final SettlementPeriodRepository periodRepository;
    private final LedgerRepository ledgerRepository;
    private final EventBus eventBus;

    public SettlementPeriodService(SettlementPeriodRepository periodRepository,
                                   LedgerRepository ledgerRepository,
                                   EventBus eventBus) {
        this.periodRepository = periodRepository;
        this.ledgerRepository = ledgerRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Long open(LocalDate start, LocalDate end) {
        SettlementPeriod p = SettlementPeriod.open(start, end);
        p.assignId(IdGenerator.next());
        periodRepository.save(p);
        return p.getId();
    }

    @Override
    public void close(Long periodId) {
        SettlementPeriod period = periodRepository.findById(periodId)
                .orElseThrow(NotFoundException::new);

        var ledgers = ledgerRepository.findByPeriodId(periodId);
        LedgerReconciliation.Result result = LedgerReconciliation.verify(ledgers);

        if (!result.balanced()) {
            // 주의: 현재 @Transactional 내에서 publish → AFTER_COMMIT 리스너는 아래 throw 로 롤백되면 소실.
            // Notification 은 AFTER_ROLLBACK 또는 별도 fallback 로 받도록 구독 설계 필요.
            eventBus.publish(new LedgerUnbalancedEvent(
                    periodId,
                    result.unbalancedRefs().stream().map(l -> l.getId()).toList(),
                    Instant.now()));
            throw new ConflictException(SettlementErrorCode.LEDGER_UNBALANCED);
        }

        period.close();
        periodRepository.save(period);
        eventBus.publish(new PeriodClosedEvent(
                periodId, period.getStartDate(), period.getEndDate(), Instant.now()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Long> currentOpenPeriodId(LocalDate at) {
        return periodRepository.findOpenContaining(at).map(SettlementPeriod::getId);
    }
}