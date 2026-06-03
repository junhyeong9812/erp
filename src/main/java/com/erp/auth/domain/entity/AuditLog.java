package com.erp.auth.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "auth_audit_log")
public class AuditLog extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String action;
    private String resource;
    private LocalDateTime occurredAt;

    protected AuditLog() {}

    public static AuditLog of(Long userId, String action, String resource) {
        AuditLog a = new AuditLog();
        a.userId = userId; a.action = action; a.resource = resource;
        a.occurredAt = LocalDateTime.now();
        return a;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
}