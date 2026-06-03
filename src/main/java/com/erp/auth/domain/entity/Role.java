package com.erp.auth.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_role")
public class Role extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;
    private String name;

    @ElementCollection
    @CollectionTable(name = "auth_role_permission", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission_code")
    private Set<String> permissionCodes = new HashSet<>();

    protected Role() {}

    public static Role of(String code, String name) {
        Role r = new Role();
        r.code = code; r.name = name;
        return r;
    }

    public void grant(String permission) { permissionCodes.add(permission); }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public Set<String> getPermissionCodes() { return permissionCodes; }
}