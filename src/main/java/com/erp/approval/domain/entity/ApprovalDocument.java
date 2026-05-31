package com.erp.approval.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.approval.domain.event.ApprovalCompletedEvent;
import com.erp.approval.domain.event.ApprovalRequestedEvent;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_document")
public class ApprovalDocument extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long drafterId;
    private String documentType;
    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_id")
    @OrderColumn(name = "step_no")
    private List<ApprovalStep> steps = new ArrayList<>();

    private int currentStep;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected ApprovalDocument() {}

    public static ApprovalDocument draft(Long drafterId, String documentType,
                                         String title, List<Long> approverIds) {
        ApprovalDocument d = new ApprovalDocument();
        d.drafterId = drafterId;
        d.documentType = documentType;
        d.title = title;
        d.currentStep = 0;
        d.status = Status.IN_PROGRESS;
        for (int i = 0; i < approverIds.size(); i++) {
            d.steps.add(ApprovalStep.pending(approverIds.get(i)));
        }
        d.register(new ApprovalRequestedEvent(null, drafterId, documentType, Instant.now()));
        return d;
    }

    public void approve(Long approverId) {
        ApprovalStep step = steps.get(currentStep);
        if (!step.getApproverId().equals(approverId)) {
            throw new IllegalStateException("현재 결재자 아님");
        }
        step.approve();
        currentStep++;
        if (currentStep >= steps.size()) {
            this.status = Status.APPROVED;
            register(new ApprovalCompletedEvent(this.id, this.documentType, true, Instant.now()));
        }
    }

    public void reject(Long approverId, String reason) {
        ApprovalStep step = steps.get(currentStep);
        if (!step.getApproverId().equals(approverId)) throw new IllegalStateException();
        step.reject(reason);
        this.status = Status.REJECTED;
        register(new ApprovalCompletedEvent(this.id, this.documentType, false, Instant.now()));
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Status getStatus() { return status; }
    public int getCurrentStep() { return currentStep; }

    public enum Status { IN_PROGRESS, APPROVED, REJECTED, CANCELLED }
}