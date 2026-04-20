package com.erp.inventory.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_stock_movement")
public class StockMovement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stockId;

    @Enumerated(EnumType.STRING)
    private Type type;

    private int quantity;
    private String reason;
    private String referenceType;
    private Long referenceId;

    protected StockMovement() {}

    public static StockMovement of(Long stockId, Type type, int quantity,
                                   String reason, String refType, Long refId) {
        StockMovement m = new StockMovement();
        m.stockId = stockId;
        m.type = type;
        m.quantity = quantity;
        m.reason = reason;
        m.referenceType = refType;
        m.referenceId = refId;
        return m;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Long getStockId() { return stockId; }
    public Type getType() { return type; }
    public int getQuantity() { return quantity; }

    public enum Type { IN, OUT, RESERVE, RELEASE, ADJUST, TRANSFER }
}