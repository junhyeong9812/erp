package com.erp.inventory.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.common.domain.Money;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    private long priceAmount;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Product() {}

    private Product(String sku, String name, Money price) {
        this.sku = sku;
        this.name = name;
        this.priceAmount = price.amount().longValueExact();
        this.status = Status.ACTIVE;
    }

    public static Product register(String sku, String name, Money price) {
        if (sku == null || sku.isBlank()) throw new IllegalArgumentException("sku required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        return new Product(sku, name, price);
    }

    public void discontinue() {
        this.status = Status.DISCONTINUED;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Money getPrice() { return Money.of(priceAmount); }
    public Status getStatus() { return status; }

    public enum Status { ACTIVE, DISCONTINUED }
}