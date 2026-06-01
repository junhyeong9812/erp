package com.erp.approval.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApprovalLineTest {

    @Test
    void of_는_결재자_리스트_순서_보존() {
        ApprovalLine line = ApprovalLine.of("EXPENSE", List.of(10L, 20L, 30L));

        assertThat(line.getApproverEmployeeIds()).containsExactly(10L, 20L, 30L);
    }

    @Test
    void of_는_입력_리스트를_방어적_복사() {
        var src = new java.util.ArrayList<>(List.of(10L, 20L));
        ApprovalLine line = ApprovalLine.of("EXPENSE", src);

        src.add(99L);

        assertThat(line.getApproverEmployeeIds()).containsExactly(10L, 20L);
    }
}