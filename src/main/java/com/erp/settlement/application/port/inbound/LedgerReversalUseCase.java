package com.erp.settlement.application.port.inbound;

public interface LedgerReversalUseCase {
    Long reverse(Long originalLedgerId, String reason);
}
