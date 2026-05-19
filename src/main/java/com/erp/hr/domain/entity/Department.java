package com.erp.hr.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "hr_department")
public class Department extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long parentId;

    protected Department() {}
    public static Department of(String name, Long parentId) {
        Department d = new Department();
        d.name = name; d.parentId = parentId;
        return d;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getName() { return name; }
}