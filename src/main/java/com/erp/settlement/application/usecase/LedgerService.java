package com.erp.settlement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.support.IdGenerator;
import com.erp.settlement.application.dto.command.CreateLedgerCommand;
import com.erp.settlement.application.port.inbound.LedgerUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.domain.entity.Ledger;
import com.erp.settlement.domain.exception.SettlementErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LedgerService implements LedgerUseCase {

    private final LedgerRepository ledgerRepository;
    private final SettlementPeriodRepository periodRepository;

    public LedgerService(LedgerRepository ledgerRepository,
                         SettlementPeriodRepository periodRepository) {
        this.ledgerRepository = ledgerRepository;
        this.periodRepository = periodRepository;
    }

    @Override
    public Long createSalesLedger(CreateLedgerCommand cmd) {
        var period = periodRepository.findById(cmd.periodId())
                .orElseThrow(() -> new NotFoundException(SettlementErrorCode.NO_OPEN_PERIOD));
        period.assertOpen();   // 마감된 기간이면 ConflictException
        Ledger ledger = Ledger.sales(cmd.referenceId(), Money.of(cmd.amount()), cmd.description(), cmd.periodId());
        ledger.assignId(IdGenerator.next());
        ledgerRepository.save(ledger);
        return ledger.getId();
    }

    @Override
    public Long createRefundLedger(CreateLedgerCommand cmd) {
        var period = periodRepository.findById(cmd.periodId())
                .orElseThrow(() -> new NotFoundException(SettlementErrorCode.NO_OPEN_PERIOD));
        period.assertOpen();
        Ledger ledger = Ledger.refund(cmd.referenceId(), Money.of(cmd.amount()), cmd.description(), cmd.periodId());
        ledger.assignId(IdGenerator.next());
        ledgerRepository.save(ledger);
        return ledger.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Money totalSales(Long periodId) {
        return ledgerRepository.findByPeriodAndType(periodId, Ledger.Type.SALES).stream()
                .map(Ledger::getCredit)
                .reduce(Money.ZERO, Money::add);
    }
}