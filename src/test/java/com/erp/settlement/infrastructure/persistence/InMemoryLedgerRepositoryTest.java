package com.erp.settlement.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.settlement.domain.entity.Ledger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLedgerRepositoryTest {

    InMemoryLedgerRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryLedgerRepository(); }

    @Test
    void save_후_findById_로_조회() {
        Ledger l = Ledger.sales(1L, Money.of(1000), "s", 1L);
        l.assignId(10L);
        repo.save(l);

        assertThat(repo.findById(10L)).isPresent();
    }

    @Test
    void findByPeriodAndType_은_기간과_타입_모두_일치() {
        Ledger s1 = Ledger.sales(1L, Money.of(1000), "", 1L); s1.assignId(1L);
        Ledger s2 = Ledger.sales(2L, Money.of(2000), "", 1L); s2.assignId(2L);
        Ledger r1 = Ledger.refund(3L, Money.of(500), "", 1L); r1.assignId(3L);
        Ledger s3 = Ledger.sales(4L, Money.of(3000), "", 2L); s3.assignId(4L);
        repo.save(s1); repo.save(s2); repo.save(r1); repo.save(s3);

        var result = repo.findByPeriodAndType(1L, Ledger.Type.SALES);

        assertThat(result).hasSize(2).extracting(Ledger::getId).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    void findByPeriodId_은_해당_기간_모든_타입_반환() {
        Ledger s = Ledger.sales(1L, Money.of(1000), "", 1L); s.assignId(1L);
        Ledger r = Ledger.refund(2L, Money.of(500), "", 1L); r.assignId(2L);
        repo.save(s); repo.save(r);

        assertThat(repo.findByPeriodId(1L)).hasSize(2);
    }
}