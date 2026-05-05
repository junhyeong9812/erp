package com.erp.procurement.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.common.domain.Money;
import jakarta.persistence.*;

@Entity
@Table(name = "procurement_supplier_quote")
public class SupplierQuote extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long supplierId;
    private Long productId;
    private int quantity;
    private long unitPrice;

    protected SupplierQuote() {}

    public static SupplierQuote of(Long supplierId, Long productId, int quantity, Money unitPrice) {
        SupplierQuote q = new SupplierQuote();
        q.supplierId = supplierId;
        q.productId = productId;
        q.quantity = quantity;
        q.unitPrice = unitPrice.amount().longValueExact();
        return q;
    }

    public Money totalAmount() { return Money.of(unitPrice * quantity); }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getSupplierId() { return supplierId; }
    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public long getUnitPrice() { return unitPrice; }
}