package com.erp.approval.infrastructure.persistence;

import com.erp.approval.application.port.outbound.ApprovalDocumentRepository;
import com.erp.approval.domain.entity.ApprovalDocument;
import com.erp.common.persistence.InMemoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryApprovalDocumentRepository extends InMemoryRepository<ApprovalDocument, Long>
        implements ApprovalDocumentRepository {
    @Override protected Long extractId(ApprovalDocument d) { return d.getId(); }
}