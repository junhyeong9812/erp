package com.erp.auth.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermissionTest {

    @Test
    void of_로_Permission_생성() {
        Permission p = Permission.of("CUSTOMER_READ", "고객 조회");

        assertThat(p.getCode()).isEqualTo("CUSTOMER_READ");
    }

    @Test
    void assignId_후_getId_반환() {
        Permission p = Permission.of("CUSTOMER_READ", "고객 조회");
        p.assignId(100L);

        assertThat(p.getId()).isEqualTo(100L);
    }
}