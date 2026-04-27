package com.erp.settlement.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LedgerReconciliationTest {
    @Test
    void 매출환불만_있는_케이스는_균형으로_본다() {
        var ls = java.util.List.of(
                Ledger.sales(1L, Money.of(1000), "", 1L),
                Ledger.refund(2L, Money.of(300), "", 1L));
        var r = com.erp.settlement.domain.service.LedgerReconciliation.verify(ls);
        assertThat(r.balanced()).isTrue();   // ADJUSTMENT/REVERSAL 만 엄격 검증
    }

    @Test
    void 조정분개에서_차대변_불일치_검출() {
        var a = Ledger.adjustment(10L, "INV", Money.of(500), Money.of(400), "", 1L);
        var r = com.erp.settlement.domain.service.LedgerReconciliation.verify(java.util.List.of(a));
        assertThat(r.balanced()).isFalse();
        assertThat(r.unbalancedRefs()).hasSize(1);
    }
}