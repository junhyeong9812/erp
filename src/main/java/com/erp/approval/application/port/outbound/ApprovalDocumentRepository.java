package com.erp.approval.application.port.outbound;

import com.erp.approval.domain.entity.ApprovalDocument;

import java.util.Optional;

public interface ApprovalDocumentRepository {
    ApprovalDocument save(ApprovalDocument document);
    Optional<ApprovalDocument> findById(Long id);
}