package com.erp.crm.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClaimTest {

    @Test
    void open_으로_생성한_Claim_은_OPEN_상태() {
        Claim c = Claim.open(1L, "배송 지연 관련 클레임");

        assertThat(c.getStatus()).isEqualTo(Claim.Status.OPEN);
    }

    @Test
    void resolve_호출_시_RESOLVED_로_전환() {
        Claim c = Claim.open(1L, "배송 지연");

        c.resolve("재배송 완료");

        assertThat(c.getStatus()).isEqualTo(Claim.Status.RESOLVED);
    }

    @Test
    void assignId_후_getId_반환() {
        Claim c = Claim.open(1L, "x");
        c.assignId(500L);

        assertThat(c.getId()).isEqualTo(500L);
    }
}