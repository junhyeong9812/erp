package com.erp.procurement.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierQuoteTest {

    @Test
    void totalAmount_은_unitPrice_곱_quantity() {
        SupplierQuote q = SupplierQuote.of(1L, 100L, 50, Money.of(1000));

        assertThat(q.totalAmount()).isEqualTo(Money.of(50_000));
    }

    @Test
    void 수량_0_은_총액_0() {
        SupplierQuote q = SupplierQuote.of(1L, 100L, 0, Money.of(1000));

        assertThat(q.totalAmount()).isEqualTo(Money.of(0));
    }

    @Test
    void supplierId_productId_가_설정된다() {
        SupplierQuote q = SupplierQuote.of(7L, 200L, 10, Money.of(500));

        assertThat(q.getSupplierId()).isEqualTo(7L);
        assertThat(q.getProductId()).isEqualTo(200L);
    }
}