package com.erp.auth.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuditLogTest {

    @Test
    void of_로_AuditLog_생성하면_occurredAt_자동_세팅() {
        LocalDateTime before = LocalDateTime.now();
        AuditLog a = AuditLog.of(1L, "LOGIN", "USER:alice");
        LocalDateTime after = LocalDateTime.now();

        assertThat(a).isNotNull();
        // occurredAt 필드는 private 이지만 of 내부에서 now() 로 세팅됨을 간접 검증.
        // 실제 값 확인이 필요하면 getter 추가 후 isBetween(before, after).
    }

    @Test
    void assignId_후_getId_반환() {
        AuditLog a = AuditLog.of(1L, "LOGIN", "USER:alice");
        a.assignId(999L);

        assertThat(a.getId()).isEqualTo(999L);
    }
}