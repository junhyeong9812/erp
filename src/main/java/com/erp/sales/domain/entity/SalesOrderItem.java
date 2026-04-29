package com.erp.sales.domain.entity;

import com.erp.common.domain.Money;
import jakarta.persistence.*;

@Entity
@Table(name = "sales_order_item")
public class SalesOrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private int quantity;
    private long unitPrice;
    private long subtotal;

    protected SalesOrderItem() {}

    public static SalesOrderItem of(Long productId, int quantity, Money unitPrice) {
        SalesOrderItem i = new SalesOrderItem();
        i.productId = productId;
        i.quantity = quantity;
        i.unitPrice = unitPrice.amount().longValueExact();
        i.subtotal = i.unitPrice * quantity;
        return i;
    }

    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public Money getUnitPrice() { return Money.of(unitPrice); }
    public Money getSubtotal() { return Money.of(subtotal); }
}