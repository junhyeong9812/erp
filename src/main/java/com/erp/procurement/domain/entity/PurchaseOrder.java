package com.erp.procurement.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.procurement.domain.event.PurchaseOrderIssuedEvent;
import com.erp.procurement.domain.event.GoodsReceivedEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "procurement_purchase_order")
public class PurchaseOrder extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long supplierId;
    private Long productId;
    private int quantity;
    private int receivedQuantity;
    private long unitPrice;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected PurchaseOrder() {}

    public static PurchaseOrder issue(Long supplierId, Long productId, int quantity, Money unitPrice) {
        PurchaseOrder po = new PurchaseOrder();
        po.supplierId = supplierId;
        po.productId = productId;
        po.quantity = quantity;
        po.receivedQuantity = 0;
        po.unitPrice = unitPrice.amount().longValueExact();
        po.status = Status.ISSUED;
        po.register(new PurchaseOrderIssuedEvent(null, supplierId, productId, quantity, Instant.now()));
        return po;
    }

    public void receive(int amount) {
        if (status == Status.COMPLETED) throw new IllegalStateException("이미 완료");
        this.receivedQuantity += amount;
        register(new GoodsReceivedEvent(this.id, this.productId, amount, Instant.now()));
        if (receivedQuantity >= quantity) {
            this.status = Status.COMPLETED;
        } else {
            this.status = Status.PARTIAL;
        }
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getSupplierId() { return supplierId; }
    public Long getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public int getReceivedQuantity() { return receivedQuantity; }
    public Money getUnitPrice() { return Money.of(unitPrice); }
    public Status getStatus() { return status; }

    public enum Status { ISSUED, PARTIAL, COMPLETED, CANCELLED }
}