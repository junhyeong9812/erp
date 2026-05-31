package com.erp.approval.presentation.api;

import com.erp.approval.application.dto.command.ApproveCommand;
import com.erp.approval.application.dto.command.DraftApprovalCommand;
import com.erp.approval.application.port.inbound.ApprovalUseCase;
import com.erp.approval.presentation.dto.request.DraftApprovalRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/approval/documents")
public class ApprovalController {

    private final ApprovalUseCase useCase;

    public ApprovalController(ApprovalUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    public ResponseEntity<Long> draft(@RequestBody DraftApprovalRequest req) {
        return ResponseEntity.ok(useCase.draft(new DraftApprovalCommand(
                req.drafterId(), req.documentType(), req.title(), req.approverIds())));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long id, @RequestParam Long approverId) {
        useCase.approve(new ApproveCommand(id, approverId));
        return ResponseEntity.ok().build();
    }
}