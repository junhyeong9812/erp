package com.erp.inventory.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Quantity;
import com.erp.inventory.domain.event.StockReservedEvent;
import com.erp.inventory.domain.event.StockDepletedEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inventory_stock",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "warehouse_id"}))
public class Stock extends AggregateRoot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    private int quantity;        // 총량
    private int reservedQuantity;// 예약량

    protected Stock() {}

    private Stock(Long productId, Long warehouseId, int initial) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = initial;
        this.reservedQuantity = 0;
    }

    public static Stock open(Long productId, Long warehouseId, Quantity initial) {
        return new Stock(productId, warehouseId, initial.value());
    }

    // 입고
    public void receive(Quantity amount) {
        this.quantity += amount.value();
    }

    // 예약 (가용량 확인)
    public void reserve(Quantity amount) {
        if (availableQuantity().value() < amount.value()) {
            throw new IllegalStateException(
                    "재고 부족: available=" + availableQuantity() + ", requested=" + amount);
        }
        this.reservedQuantity += amount.value();
        register(new StockReservedEvent(this.id, this.productId, amount.value(), Instant.now()));

        if (availableQuantity().isZero()) {
            register(new StockDepletedEvent(this.id, this.productId, Instant.now()));
        }
    }

    // 출고 (예약 소진)
    public void ship(Quantity amount) {
        if (reservedQuantity < amount.value()) {
            throw new IllegalStateException(
                    "예약량 부족: reserved=" + reservedQuantity + ", requested=" + amount);
        }
        this.reservedQuantity -= amount.value();
        this.quantity -= amount.value();
    }

    // 예약 취소
    public void release(Quantity amount) {
        if (reservedQuantity < amount.value()) {
            throw new IllegalStateException("예약량보다 많이 취소할 수 없음");
        }
        this.reservedQuantity -= amount.value();
    }

    public Quantity availableQuantity() {
        return Quantity.of(quantity - reservedQuantity);
    }

    public Quantity totalQuantity() { return Quantity.of(quantity); }
    public Quantity reservedQuantity() { return Quantity.of(reservedQuantity); }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getWarehouseId() { return warehouseId; }
}