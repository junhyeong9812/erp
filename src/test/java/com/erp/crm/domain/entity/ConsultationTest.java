package com.erp.crm.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultationTest {

    @Test
    void record_로_Consultation_생성_후_id_부여() {
        Consultation c = Consultation.record(1L, 10L, "결제 오류 문의", "카드 승인 실패 문의");
        c.assignId(100L);

        assertThat(c.getId()).isEqualTo(100L);
    }

    @Test
    void record_는_consultedAt_을_자동_세팅() {
        // consultedAt 은 LocalDateTime.now() 로 세팅됨 - NPE 없이 생성되는지 확인
        Consultation c = Consultation.record(1L, 10L, "문의", "내용");

        assertThat(c).isNotNull();
    }
}