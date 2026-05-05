package com.erp.sales.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.sales.domain.entity.SalesOrder;
import com.erp.sales.domain.entity.SalesOrderItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemorySalesOrderRepositoryTest {

    @Test
    void save_후_findById_로_복원() {
        InMemorySalesOrderRepository repo = new InMemorySalesOrderRepository();
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000))
        ));
        order.assignId(1L);

        repo.save(order);

        assertThat(repo.findById(1L)).isPresent()
                .get().extracting(SalesOrder::getCustomerId).isEqualTo(1L);
    }

    @Test
    void id_없이_save_하면_IllegalState() {
        InMemorySalesOrderRepository repo = new InMemorySalesOrderRepository();
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000))
        ));
        // assignId 호출 안 함

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> repo.save(order))
                .isInstanceOf(IllegalStateException.class);
    }
}