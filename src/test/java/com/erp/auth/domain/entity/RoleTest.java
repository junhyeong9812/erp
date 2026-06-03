package com.erp.auth.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    void of_로_Role_생성() {
        Role r = Role.of("ROLE_SALES", "영업");

        assertThat(r.getCode()).isEqualTo("ROLE_SALES");
        assertThat(r.getName()).isEqualTo("영업");
        assertThat(r.getPermissionCodes()).isEmpty();
    }

    @Test
    void grant_로_권한_추가() {
        Role r = Role.of("ROLE_SALES", "영업");
        r.grant("CUSTOMER_READ");
        r.grant("CUSTOMER_WRITE");

        assertThat(r.getPermissionCodes())
                .containsExactlyInAnyOrder("CUSTOMER_READ", "CUSTOMER_WRITE");
    }

    @Test
    void grant_중복_권한은_Set_특성상_한_번만_보관() {
        Role r = Role.of("ROLE_SALES", "영업");
        r.grant("CUSTOMER_READ");
        r.grant("CUSTOMER_READ");

        assertThat(r.getPermissionCodes()).hasSize(1);
    }
}