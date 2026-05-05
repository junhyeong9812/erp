package com.erp.sales.domain.entity;

import com.erp.common.domain.Money;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SalesOrderTest {

    @Test
    void place_는_items_합계를_totalAmount_로() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000)),
                SalesOrderItem.of(200L, 3, Money.of(500))
        ));

        // 2 * 1000 + 3 * 500 = 3500
        assertThat(order.getTotalAmount()).isEqualTo(Money.of(3500));
        assertThat(order.getStatus()).isEqualTo(SalesOrder.Status.PLACED);
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    void place_시_SalesOrderPlacedEvent_발행() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000))
        ));

        assertThat(order.events()).hasAtLeastOneElementOfType(SalesOrderPlacedEvent.class);
    }

    @Test
    void 이벤트의_Line_은_productId_와_quantity_만() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000)),
                SalesOrderItem.of(200L, 5, Money.of(300))
        ));

        SalesOrderPlacedEvent event = (SalesOrderPlacedEvent) order.events().stream()
                .filter(e -> e instanceof SalesOrderPlacedEvent)
                .findFirst().orElseThrow();

        assertThat(event.customerId()).isEqualTo(1L);
        assertThat(event.lines()).extracting(SalesOrderPlacedEvent.Line::productId)
                .containsExactly(100L, 200L);
        assertThat(event.lines()).extracting(SalesOrderPlacedEvent.Line::quantity)
                .containsExactly(2, 5);
    }

    @Test
    void getItems_는_불변_View() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000))
        ));

        var items = order.getItems();

        assertThat(items).hasSize(1);
        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> items.add(SalesOrderItem.of(999L, 1, Money.of(1))))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}