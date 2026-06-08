package com.erp.notification.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationLogTest {

    @Test
    void of_로_로그_생성_및_id_할당() {
        NotificationLog log = NotificationLog.of(10L, "SENT", "ok");
        log.assignId(1L);

        assertThat(log.getId()).isEqualTo(1L);
    }
}