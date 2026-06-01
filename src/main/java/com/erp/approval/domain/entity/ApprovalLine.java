package com.erp.approval.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "approval_line")
public class ApprovalLine extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String documentType;

    @ElementCollection
    @CollectionTable(name = "approval_line_approver", joinColumns = @JoinColumn(name = "line_id"))
    @OrderColumn(name = "step_no")
    private List<Long> approverEmployeeIds = new ArrayList<>();

    protected ApprovalLine() {}

    public static ApprovalLine of(String documentType, List<Long> approverEmployeeIds) {
        ApprovalLine l = new ApprovalLine();
        l.documentType = documentType;
        l.approverEmployeeIds = new ArrayList<>(approverEmployeeIds);
        return l;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public List<Long> getApproverEmployeeIds() { return approverEmployeeIds; }
}