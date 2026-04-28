package com.erp.settlement.application.port.inbound;

import com.erp.common.domain.Money;
import com.erp.settlement.application.dto.command.CreateLedgerCommand;

public interface LedgerUseCase {
    Long createSalesLedger(CreateLedgerCommand command);
    Long createRefundLedger(CreateLedgerCommand command);
    Money totalSales(Long periodId);
}