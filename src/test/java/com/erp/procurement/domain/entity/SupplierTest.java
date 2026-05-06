package com.erp.procurement.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SupplierTest {

    @Test
    void register_는_code_name_contact_를_설정() {
        Supplier s = Supplier.register("SUP-001", "알파상사", "02-0000-0000");

        assertThat(s.getCode()).isEqualTo("SUP-001");
        assertThat(s.getName()).isEqualTo("알파상사");
    }

    @Test
    void assignId_로_id_설정_후_조회() {
        Supplier s = Supplier.register("SUP-002", "베타상사", "02-1111-1111");
        s.assignId(42L);

        assertThat(s.getId()).isEqualTo(42L);
    }
}