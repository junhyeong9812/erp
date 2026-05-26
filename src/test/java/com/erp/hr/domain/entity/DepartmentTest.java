package com.erp.hr.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentTest {

    @Test
    void of_는_최상위_부서_생성_parentId_null_허용() {
        Department d = Department.of("본사", null);

        assertThat(d.getName()).isEqualTo("본사");
    }

    @Test
    void assignId_후_getId_조회() {
        Department d = Department.of("영업팀", 1L);
        d.assignId(100L);

        assertThat(d.getId()).isEqualTo(100L);
    }
}