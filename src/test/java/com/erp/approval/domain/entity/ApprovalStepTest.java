package com.erp.approval.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApprovalStepTest {

    @Test
    void pending_는_PENDING_상태로_생성() {
        ApprovalStep s = ApprovalStep.pending(10L);

        assertThat(s.getApproverId()).isEqualTo(10L);
        assertThat(s.getDecision()).isEqualTo(ApprovalStep.Decision.PENDING);
    }

    @Test
    void approve_는_APPROVED_로_전이() {
        ApprovalStep s = ApprovalStep.pending(10L);

        s.approve();

        assertThat(s.getDecision()).isEqualTo(ApprovalStep.Decision.APPROVED);
    }

    @Test
    void reject_는_REJECTED_로_전이() {
        ApprovalStep s = ApprovalStep.pending(10L);

        s.reject("사유");

        assertThat(s.getDecision()).isEqualTo(ApprovalStep.Decision.REJECTED);
    }
}