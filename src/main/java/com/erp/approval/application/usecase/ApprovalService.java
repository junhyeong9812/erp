package com.erp.approval.application.usecase;

import com.erp.approval.application.dto.command.ApproveCommand;
import com.erp.approval.application.dto.command.DraftApprovalCommand;
import com.erp.approval.application.port.inbound.ApprovalUseCase;
import com.erp.approval.application.port.outbound.ApprovalDocumentRepository;
import com.erp.approval.domain.entity.ApprovalDocument;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApprovalService implements ApprovalUseCase {

    private final ApprovalDocumentRepository repo;
    private final EventBus eventBus;

    public ApprovalService(ApprovalDocumentRepository repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    @Override
    public Long draft(DraftApprovalCommand cmd) {
        ApprovalDocument d = ApprovalDocument.draft(cmd.drafterId(), cmd.documentType(),
                cmd.title(), cmd.approverIds());
        d.assignId(IdGenerator.next());
        repo.save(d);
        eventBus.publishAll(d.pullEvents());
        return d.getId();
    }

    @Override
    public void approve(ApproveCommand cmd) {
        ApprovalDocument d = repo.findById(cmd.documentId()).orElseThrow(NotFoundException::new);
        d.approve(cmd.approverId());
        repo.save(d);
        eventBus.publishAll(d.pullEvents());
    }

    @Override
    public void reject(Long documentId, Long approverId, String reason) {
        ApprovalDocument d = repo.findById(documentId).orElseThrow(NotFoundException::new);
        d.reject(approverId, reason);
        repo.save(d);
        eventBus.publishAll(d.pullEvents());
    }
}