package com.erp.settlement.domain.service;

import com.erp.common.domain.Money;
import com.erp.settlement.domain.entity.Ledger;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerReconciliationTest {

    @Test
    void 매출과_환불만_있는_경우_ADJUSTMENT_부재로_balanced() {
        List<Ledger> ls = List.of(
                Ledger.sales(1L, Money.of(1000), "", 1L),
                Ledger.refund(2L, Money.of(300), "", 1L));

        var r = LedgerReconciliation.verify(ls);

        assertThat(r.balanced()).isTrue();
        assertThat(r.unbalancedRefs()).isEmpty();
    }

    @Test
    void 조정분개_차대변_동일금액이면_balanced() {
        Ledger adj = Ledger.adjustment(10L, "INV",
                Money.of(500), Money.of(500), "감가", 1L);

        var r = LedgerReconciliation.verify(List.of(adj));

        assertThat(r.balanced()).isTrue();
    }

    @Test
    void 조정분개_차대변_불일치를_검출() {
        Ledger adj = Ledger.adjustment(10L, "INV",
                Money.of(500), Money.of(400), "오기표", 1L);

        var r = LedgerReconciliation.verify(List.of(adj));

        assertThat(r.balanced()).isFalse();
        assertThat(r.unbalancedRefs()).hasSize(1);
    }

    @Test
    void 원본_조정분개와_반대전표가_합쳐지면_균형_회복() {
        Ledger original = Ledger.adjustment(10L, "INV",
                Money.of(500), Money.of(400), "원본", 1L);
        original.assignId(100L);
        // reverseOf 는 referenceType="LEDGER", referenceId=원본id 로 그룹이 달라짐 →
        // 따라서 여기서는 동일 그룹 내 복구를 수동으로 adjustment 2건으로 시뮬레이트
        Ledger fix = Ledger.adjustment(10L, "INV",
                Money.of(0), Money.of(100), "보정", 1L);

        var r = LedgerReconciliation.verify(List.of(original, fix));

        assertThat(r.balanced()).isTrue();
    }

    @Test
    void totalDebit_과_totalCredit_은_전체_합산() {
        List<Ledger> ls = List.of(
                Ledger.sales(1L, Money.of(1000), "", 1L),
                Ledger.refund(2L, Money.of(300), "", 1L),
                Ledger.adjustment(10L, "INV", Money.of(200), Money.of(200), "", 1L));

        var r = LedgerReconciliation.verify(ls);

        assertThat(r.totalDebit()).isEqualTo(Money.of(500));     // refund 300 + adj 200
        assertThat(r.totalCredit()).isEqualTo(Money.of(1200));   // sales 1000 + adj 200
    }
}