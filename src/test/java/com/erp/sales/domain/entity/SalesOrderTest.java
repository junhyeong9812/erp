package com.erp.sales.domain.entity;

import com.erp.common.domain.Money;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void place_단독으로는_이벤트가_등록되지_않는다() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000))
        ));

        assertThat(order.events()).isEmpty();
    }

    @Test
    void registerPlacedEvent_호출_시_orderId_가_채워진_이벤트가_등록된다() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000)),
                SalesOrderItem.of(200L, 5, Money.of(300))
        ));
        order.assignId(42L);
        order.registerPlacedEvent();

        SalesOrderPlacedEvent event = (SalesOrderPlacedEvent) order.events().stream()
                .filter(e -> e instanceof SalesOrderPlacedEvent)
                .findFirst().orElseThrow();

        assertThat(event.orderId()).isEqualTo(42L);
        assertThat(event.customerId()).isEqualTo(1L);
        assertThat(event.lines()).extracting(SalesOrderPlacedEvent.Line::productId)
                .containsExactly(100L, 200L);
        assertThat(event.lines()).extracting(SalesOrderPlacedEvent.Line::quantity)
                .containsExactly(2, 5);
    }

    @Test
    void registerPlacedEvent_는_assignId_전에_호출하면_IllegalStateException() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 1, Money.of(1000))
        ));

        assertThatThrownBy(order::registerPlacedEvent)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 상태_전이_PLACED_to_CONFIRMED_to_SHIPPED_to_COMPLETED() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 1, Money.of(1000))
        ));

        order.confirm();
        assertThat(order.getStatus()).isEqualTo(SalesOrder.Status.CONFIRMED);

        order.ship();
        assertThat(order.getStatus()).isEqualTo(SalesOrder.Status.SHIPPED);

        order.complete();
        assertThat(order.getStatus()).isEqualTo(SalesOrder.Status.COMPLETED);
    }

    @Test
    void confirm_은_PLACED_상태에서만_허용() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 1, Money.of(1000))
        ));
        order.confirm();   // PLACED -> CONFIRMED

        assertThatThrownBy(order::confirm)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancel_은_종결된_주문에서_불가() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 1, Money.of(1000))
        ));
        order.cancel();
        assertThat(order.getStatus()).isEqualTo(SalesOrder.Status.CANCELLED);

        assertThatThrownBy(order::cancel)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getItems_는_불변_View() {
        SalesOrder order = SalesOrder.place(1L, 10L, List.of(
                SalesOrderItem.of(100L, 2, Money.of(1000))
        ));

        var items = order.getItems();

        assertThat(items).hasSize(1);
        assertThatThrownBy(() -> items.add(SalesOrderItem.of(999L, 1, Money.of(1))))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
