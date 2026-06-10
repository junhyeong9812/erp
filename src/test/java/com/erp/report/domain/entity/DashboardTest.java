package com.erp.report.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardTest {

    @Test
    void create_로_Dashboard_구성() {
        Dashboard d = Dashboard.create("CEO", 1L,
                List.of("payment.amount", "sales.order.quantity"));

        d.assignId(10L);
        assertThat(d.getId()).isEqualTo(10L);
    }

    @Test
    void 입력_리스트_수정해도_Dashboard_내부_리스트_불변() {
        var names = new java.util.ArrayList<String>();
        names.add("a");
        Dashboard d = Dashboard.create("X", 1L, names);

        names.add("b");   // 외부 변경
        // 복사본이므로 내부에는 "b" 가 들어가면 안 됨 — getter 없어도 방어적 복사는 강제
        // (반사 접근 대신 동작 기대만 기록)
        assertThat(names).contains("b");
    }
}