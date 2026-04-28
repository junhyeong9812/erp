package com.erp.settlement.presentation.api;

import com.erp.common.domain.Money;
import com.erp.settlement.application.port.inbound.LedgerUseCase;
import com.erp.settlement.presentation.dto.response.LedgerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlement")
public class LedgerController {

    private final LedgerUseCase ledgerUseCase;

    public LedgerController(LedgerUseCase ledgerUseCase) {
        this.ledgerUseCase = ledgerUseCase;
    }

    @GetMapping("/periods/{periodId}/sales-total")
    public ResponseEntity<LedgerResponse> totalSales(@PathVariable Long periodId) {
        Money total = ledgerUseCase.totalSales(periodId);
        return ResponseEntity.ok(new LedgerResponse(periodId, total.amount().longValueExact()));
    }
}