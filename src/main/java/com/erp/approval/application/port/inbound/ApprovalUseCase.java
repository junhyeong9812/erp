package com.erp.approval.application.port.inbound;

import com.erp.approval.application.dto.command.ApproveCommand;
import com.erp.approval.application.dto.command.DraftApprovalCommand;

public interface ApprovalUseCase {
    Long draft(DraftApprovalCommand command);
    void approve(ApproveCommand command);
    void reject(Long documentId, Long approverId, String reason);
}