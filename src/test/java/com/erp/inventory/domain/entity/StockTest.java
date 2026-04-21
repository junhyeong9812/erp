package com.erp.inventory.domain.entity;

import com.erp.common.domain.Quantity;
import com.erp.inventory.domain.event.StockReservedEvent;
import com.erp.inventory.domain.event.StockDepletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class StockTest {

    @Test
    @DisplayName("초기 재고는 총량=입고량, 예약=0, 가용량=총량")
    void 초기_재고() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));

        assertThat(stock.totalQuantity().value()).isEqualTo(10);
        assertThat(stock.reservedQuantity().value()).isEqualTo(0);
        assertThat(stock.availableQuantity().value()).isEqualTo(10);
    }

    @Test
    @DisplayName("입고하면 총량이 증가하고 가용량도 같이 증가한다")
    void 입고() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));
        stock.receive(Quantity.of(5));

        assertThat(stock.totalQuantity().value()).isEqualTo(15);
        assertThat(stock.availableQuantity().value()).isEqualTo(15);
    }

    @Test
    @DisplayName("예약하면 가용량이 줄고 예약량이 늘어난다")
    void 예약() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));
        stock.reserve(Quantity.of(3));

        assertThat(stock.availableQuantity().value()).isEqualTo(7);
        assertThat(stock.reservedQuantity().value()).isEqualTo(3);
        assertThat(stock.totalQuantity().value()).isEqualTo(10);
    }

    @Test
    @DisplayName("가용량보다 많이 예약하면 예외")
    void 초과_예약() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));

        assertThatThrownBy(() -> stock.reserve(Quantity.of(11)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("재고 부족");
    }

    @Test
    @DisplayName("예약 시 StockReservedEvent 가 발행된다")
    void 예약_이벤트() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));
        stock.reserve(Quantity.of(3));

        assertThat(stock.events())
                .hasAtLeastOneElementOfType(StockReservedEvent.class);
    }

    @Test
    @DisplayName("가용량이 0 이 되면 StockDepletedEvent 도 같이 나간다")
    void 소진_이벤트() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(3));
        stock.reserve(Quantity.of(3));

        assertThat(stock.events())
                .hasAtLeastOneElementOfType(StockDepletedEvent.class);
    }

    @Test
    @DisplayName("출고는 예약된 만큼만 가능")
    void 출고() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));
        stock.reserve(Quantity.of(3));
        stock.ship(Quantity.of(3));

        assertThat(stock.totalQuantity().value()).isEqualTo(7);
        assertThat(stock.reservedQuantity().value()).isEqualTo(0);
    }

    @Test
    @DisplayName("예약되지 않은 수량은 출고 불가")
    void 출고_예약없음() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));

        assertThatThrownBy(() -> stock.ship(Quantity.of(1)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("예약 취소는 예약량을 줄인다")
    void 예약_취소() {
        Stock stock = Stock.open(1L, 1L, Quantity.of(10));
        stock.reserve(Quantity.of(3));
        stock.release(Quantity.of(2));

        assertThat(stock.reservedQuantity().value()).isEqualTo(1);
        assertThat(stock.availableQuantity().value()).isEqualTo(9);
    }
}