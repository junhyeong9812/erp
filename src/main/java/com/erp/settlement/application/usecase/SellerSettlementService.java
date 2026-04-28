package com.erp.settlement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.settlement.application.port.inbound.SellerSettlementUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SellerSettlementRepository;
import com.erp.settlement.domain.entity.Ledger;
import com.erp.settlement.domain.entity.SellerSettlement;
import com.erp.settlement.domain.event.SellerSettlementCalculatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SellerSettlementService implements SellerSettlementUseCase {

    private final SellerSettlementRepository repo;
    private final LedgerRepository ledgerRepository;
    private final EventBus eventBus;

    public SellerSettlementService(SellerSettlementRepository repo,
                                   LedgerRepository ledgerRepository,
                                   EventBus eventBus) {
        this.repo = repo; this.ledgerRepository = ledgerRepository; this.eventBus = eventBus;
    }

    @Override
    public Long calculate(Long sellerId, Long periodId) {
        var ledgers = ledgerRepository.findBySellerInPeriod(sellerId, periodId);
        Money gross = ledgers.stream()
                .filter(l -> l.getType() == Ledger.Type.SALES)
                .map(Ledger::getCredit).reduce(Money.ZERO, Money::add);
        Money refund = ledgers.stream()
                .filter(l -> l.getType() == Ledger.Type.REFUND)
                .map(Ledger::getDebit).reduce(Money.ZERO, Money::add);
        Money fee = ledgers.stream()
                .filter(l -> l.getType() == Ledger.Type.FEE)
                .map(Ledger::getDebit).reduce(Money.ZERO, Money::add);

        var settlement = SellerSettlement.calculate(sellerId, periodId, gross, refund, fee);
        settlement.assignId(IdGenerator.next());
        repo.save(settlement);
        eventBus.publish(new SellerSettlementCalculatedEvent(
                settlement.getId(), sellerId, periodId, settlement.getNetPayout().amount().longValueExact(),
                java.time.Instant.now()));
        return settlement.getId();
    }

    @Override
    public void markPaid(Long settlementId) {
        var s = repo.findById(settlementId).orElseThrow(NotFoundException::new);
        s.markPaid();
        repo.save(s);
    }
}