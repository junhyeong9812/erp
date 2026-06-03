package com.erp.approval.infrastructure.persistence;

import com.erp.approval.domain.entity.ApprovalDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryApprovalDocumentRepositoryTest {

    @Test
    void save_후_findById_로_조회() {
        InMemoryApprovalDocumentRepository repo = new InMemoryApprovalDocumentRepository();
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "-", List.of(10L));
        d.assignId(100L);

        repo.save(d);

        assertThat(repo.findById(100L)).isPresent();
    }

    @Test
    void 없는_ID_조회시_Optional_empty() {
        InMemoryApprovalDocumentRepository repo = new InMemoryApprovalDocumentRepository();

        assertThat(repo.findById(999L)).isEmpty();
    }
}