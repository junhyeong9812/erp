package com.erp.settlement.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LedgerTest {

    @Test
    void 매출전표는_credit_에_기록되고_debit_은_0() {
        Ledger l = Ledger.sales(100L, Money.of(5000), "order-1", 1L);

        assertThat(l.getType()).isEqualTo(Ledger.Type.SALES);
        assertThat(l.getCredit()).isEqualTo(Money.of(5000));
        assertThat(l.getDebit()).isEqualTo(Money.ZERO);
        assertThat(l.getReferenceType()).isEqualTo("PAYMENT");
        assertThat(l.getReferenceId()).isEqualTo(100L);
        assertThat(l.getPeriodId()).isEqualTo(1L);
    }

    @Test
    void 환불전표는_debit_에_기록되고_credit_은_0() {
        Ledger l = Ledger.refund(200L, Money.of(2000), "refund-1", 1L);

        assertThat(l.getType()).isEqualTo(Ledger.Type.REFUND);
        assertThat(l.getDebit()).isEqualTo(Money.of(2000));
        assertThat(l.getCredit()).isEqualTo(Money.ZERO);
        assertThat(l.getReferenceType()).isEqualTo("REFUND");
    }

    @Test
    void 조정분개는_차대변_양쪽을_동시에_가진다() {
        Ledger l = Ledger.adjustment(10L, "INV",
                Money.of(1000), Money.of(1000), "감가상각", 1L);

        assertThat(l.getType()).isEqualTo(Ledger.Type.ADJUSTMENT);
        assertThat(l.getDebit()).isEqualTo(Money.of(1000));
        assertThat(l.getCredit()).isEqualTo(Money.of(1000));
        assertThat(l.getReferenceType()).isEqualTo("INV");
    }

    @Test
    void 반대전표는_원본의_차대변을_뒤집는다() {
        Ledger original = Ledger.adjustment(10L, "INV",
                Money.of(1000), Money.of(700), "원본", 1L);
        original.assignId(100L);

        Ledger reversed = Ledger.reverseOf(original, "오기표", 1L);

        assertThat(reversed.getType()).isEqualTo(Ledger.Type.REVERSAL);
        assertThat(reversed.getDebit()).isEqualTo(Money.of(700));    // 원본 credit
        assertThat(reversed.getCredit()).isEqualTo(Money.of(1000));  // 원본 debit
        assertThat(reversed.getReferenceType()).isEqualTo("LEDGER");
        assertThat(reversed.getReferenceId()).isEqualTo(100L);
    }

    @Test
    void 반대전표_설명에_원본_id_가_포함된다() {
        Ledger original = Ledger.sales(1L, Money.of(500), "s", 1L);
        original.assignId(42L);

        Ledger reversed = Ledger.reverseOf(original, "취소", 1L);

        assertThat(reversed.getDescription()).contains("REVERSAL", "취소", "#42");
    }

    @Test
    void 반대전표는_매출전표에도_적용되어_credit_을_debit_으로() {
        Ledger sales = Ledger.sales(1L, Money.of(5000), "sale", 1L);
        sales.assignId(10L);

        Ledger reversed = Ledger.reverseOf(sales, "환불", 1L);

        assertThat(reversed.getDebit()).isEqualTo(Money.of(5000));
        assertThat(reversed.getCredit()).isEqualTo(Money.ZERO);
    }
}