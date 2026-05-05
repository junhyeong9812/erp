package com.erp.sales.domain.entity;

import com.erp.common.domain.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SalesOrderItemTest {

    @Test
    void of_는_subtotal_을_unitPrice_곱_quantity_로() {
        SalesOrderItem item = SalesOrderItem.of(100L, 4, Money.of(2500));

        assertThat(item.getProductId()).isEqualTo(100L);
        assertThat(item.getQuantity()).isEqualTo(4);
        assertThat(item.getUnitPrice()).isEqualTo(Money.of(2500));
        assertThat(item.getSubtotal()).isEqualTo(Money.of(10000));
    }

    @Test
    void 수량_0_은_subtotal_0() {
        SalesOrderItem item = SalesOrderItem.of(100L, 0, Money.of(1000));

        assertThat(item.getSubtotal()).isEqualTo(Money.of(0));
    }
}