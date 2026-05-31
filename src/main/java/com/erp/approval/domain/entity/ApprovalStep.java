package com.erp.approval.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_step")
public class ApprovalStep {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long approverId;

    @Enumerated(EnumType.STRING)
    private Decision decision;

    private String rejectReason;
    private LocalDateTime decidedAt;

    protected ApprovalStep() {}

    public static ApprovalStep pending(Long approverId) {
        ApprovalStep s = new ApprovalStep();
        s.approverId = approverId;
        s.decision = Decision.PENDING;
        return s;
    }

    public void approve() {
        this.decision = Decision.APPROVED;
        this.decidedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.decision = Decision.REJECTED;
        this.rejectReason = reason;
        this.decidedAt = LocalDateTime.now();
    }

    public Long getApproverId() { return approverId; }
    public Decision getDecision() { return decision; }

    public enum Decision { PENDING, APPROVED, REJECTED }
}