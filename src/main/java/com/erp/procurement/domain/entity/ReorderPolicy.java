package com.erp.procurement.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "procurement_reorder_policy")
public class ReorderPolicy extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long productId;
    private Long defaultSupplierId;
    private int reorderQuantity;

    protected ReorderPolicy() {}

    public static ReorderPolicy of(Long productId, Long defaultSupplierId, int reorderQuantity) {
        if (reorderQuantity <= 0) throw new IllegalArgumentException("reorderQuantity > 0");
        ReorderPolicy p = new ReorderPolicy();
        p.productId = productId;
        p.defaultSupplierId = defaultSupplierId;
        p.reorderQuantity = reorderQuantity;
        return p;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public Long getDefaultSupplierId() { return defaultSupplierId; }
    public int getReorderQuantity() { return reorderQuantity; }
}
