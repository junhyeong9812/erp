package com.erp.settlement.application.usecase;

import com.erp.common.exception.ConflictException;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.payment.domain.event.RefundCompletedEvent;
import com.erp.settlement.application.dto.command.CreateLedgerCommand;
import com.erp.settlement.application.port.inbound.LedgerUseCase;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import com.erp.settlement.domain.exception.SettlementErrorCode;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventHandler {

    private final LedgerUseCase ledgerUseCase;
    private final SettlementPeriodUseCase periodUseCase;

    public PaymentEventHandler(LedgerUseCase ledgerUseCase,
                               SettlementPeriodUseCase periodUseCase) {
        this.ledgerUseCase = ledgerUseCase;
        this.periodUseCase = periodUseCase;
    }

    @ApplicationModuleListener
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        Long periodId = periodUseCase.currentOpenPeriodId(java.time.LocalDate.now())
                .orElseThrow(() -> new ConflictException(SettlementErrorCode.NO_OPEN_PERIOD));
        ledgerUseCase.createSalesLedger(new CreateLedgerCommand(
                event.paymentId(), event.amount(), "Payment #" + event.paymentId(), periodId));
    }

    @ApplicationModuleListener
    public void onRefundCompleted(RefundCompletedEvent event) {
        Long periodId = periodUseCase.currentOpenPeriodId(java.time.LocalDate.now())
                .orElseThrow(() -> new ConflictException(SettlementErrorCode.NO_OPEN_PERIOD));
        ledgerUseCase.createRefundLedger(new CreateLedgerCommand(
                event.refundId(), event.amount(), "Refund #" + event.refundId(), periodId));
    }
}