package com.erp.settlement.application.usecase;

import com.erp.common.exception.ConflictException;
import com.erp.common.exception.NotFoundException;
import com.erp.common.support.IdGenerator;
import com.erp.settlement.application.port.inbound.LedgerReversalUseCase;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.domain.entity.Ledger;
import com.erp.settlement.domain.exception.SettlementErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LedgerReversalService implements LedgerReversalUseCase {

    private final LedgerRepository ledgerRepository;
    private final SettlementPeriodUseCase periodUseCase;

    public LedgerReversalService(LedgerRepository ledgerRepository,
                                 SettlementPeriodUseCase periodUseCase) {
        this.ledgerRepository = ledgerRepository;
        this.periodUseCase = periodUseCase;
    }

    @Override
    public Long reverse(Long originalLedgerId, String reason) {
        if (ledgerRepository.existsReversalOf(originalLedgerId)) {
            throw new ConflictException(SettlementErrorCode.REVERSAL_DUPLICATE);
        }
        Ledger original = ledgerRepository.findById(originalLedgerId)
                .orElseThrow(NotFoundException::new);
        Long periodId = periodUseCase.currentOpenPeriodId(java.time.LocalDate.now())
                .orElseThrow(() -> new ConflictException(SettlementErrorCode.NO_OPEN_PERIOD));

        Ledger reversal = Ledger.reverseOf(original, reason, periodId);
        reversal.assignId(IdGenerator.next());
        ledgerRepository.save(reversal);
        return reversal.getId();
    }
}