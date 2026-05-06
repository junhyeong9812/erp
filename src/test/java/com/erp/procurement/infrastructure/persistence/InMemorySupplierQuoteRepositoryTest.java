package com.erp.procurement.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.procurement.domain.entity.SupplierQuote;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemorySupplierQuoteRepositoryTest {

    @Test
    void productId_와_supplierId_로_견적_조회() {
        InMemorySupplierQuoteRepository repo = new InMemorySupplierQuoteRepository();
        SupplierQuote q = SupplierQuote.of(7L, 100L, 50, Money.of(1234));
        q.assignId(1L);
        repo.save(q);

        assertThat(repo.findLatestByProductAndSupplier(100L, 7L))
                .isPresent()
                .get().extracting(SupplierQuote::getUnitPrice).isEqualTo(1234L);
    }

    @Test
    void 다른_supplier_의_견적은_노출되지_않는다() {
        InMemorySupplierQuoteRepository repo = new InMemorySupplierQuoteRepository();
        SupplierQuote q = SupplierQuote.of(8L, 100L, 50, Money.of(999));
        q.assignId(1L);
        repo.save(q);

        assertThat(repo.findLatestByProductAndSupplier(100L, 7L)).isEmpty();
    }

    @Test
    void 견적이_없으면_empty() {
        InMemorySupplierQuoteRepository repo = new InMemorySupplierQuoteRepository();

        assertThat(repo.findLatestByProductAndSupplier(100L, 7L)).isEmpty();
    }
}