package com.erp.procurement.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "procurement_supplier")
public class Supplier extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;
    private String name;
    private String contact;

    protected Supplier() {}

    public static Supplier register(String code, String name, String contact) {
        Supplier s = new Supplier();
        s.code = code;
        s.name = name;
        s.contact = contact;
        return s;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
}