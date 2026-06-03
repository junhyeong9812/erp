package com.erp.auth.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "auth_permission")
public class Permission extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;
    private String description;

    protected Permission() {}

    public static Permission of(String code, String description) {
        Permission p = new Permission();
        p.code = code; p.description = description;
        return p;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getCode() { return code; }
}