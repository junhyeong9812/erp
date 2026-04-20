package com.erp.inventory.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory_warehouse")
public class Warehouse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    private Type type;

    protected Warehouse() {}

    private Warehouse(String name, String location, Type type) {
        this.name = name;
        this.location = location;
        this.type = type;
    }

    public static Warehouse open(String name, String location, Type type) {
        return new Warehouse(name, location, type);
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Type getType() { return type; }

    public enum Type { MAIN, SUB, RETURN }
}