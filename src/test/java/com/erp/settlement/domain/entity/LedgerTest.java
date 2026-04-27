package com.erp.settlement.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class LedgerTest {

    @Test
    void 매출_전표는_credit_에_기록() {
        Ledger l = Ledger.sales(100L, Money.of(5000), "order-1", 1L);
        assertThat(l.getCredit()).isEqualTo(Money.of(5000));
        assertThat(l.getDebit()).isEqualTo(Money.ZERO);
    }

    @Test
    void 환불_전표는_debit_에_기록() {
        Ledger l = Ledger.refund(200L, Money.of(2000), "refund-1", 1L);
        assertThat(l.getDebit()).isEqualTo(Money.of(2000));
    }

    @Test
    void 반대전표는_원본의_차대변을_뒤집는다() {
        Ledger original = Ledger.adjustment(10L, "INV", Money.of(1000), Money.of(1000), "감가", 1L);
        original.assignId(100L);

        Ledger reversed = Ledger.reverseOf(original, "오기표", 1L);

        assertThat(reversed.getDebit()).isEqualTo(Money.of(1000));   // 원본 credit
        assertThat(reversed.getCredit()).isEqualTo(Money.of(1000));  // 원본 debit
        assertThat(reversed.getType()).isEqualTo(Ledger.Type.REVERSAL);
    }
}
