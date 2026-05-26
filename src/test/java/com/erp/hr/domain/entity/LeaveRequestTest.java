package com.erp.hr.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class LeaveRequestTest {

    @Test
    void request_는_PENDING_으로_생성() {
        LeaveRequest r = LeaveRequest.request(1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), "개인사");

        assertThat(r.getStatus()).isEqualTo(LeaveRequest.Status.PENDING);
        assertThat(r.getEmployeeId()).isEqualTo(1L);
    }

    @Test
    void days_는_start_와_end_포함_일수() {
        LeaveRequest r = LeaveRequest.request(1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), "x");

        // 5/1, 5/2, 5/3 → 3일
        assertThat(r.days()).isEqualTo(3);
    }

    @Test
    void days_같은_날_시작끝은_1일() {
        LeaveRequest r = LeaveRequest.request(1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 1), "x");

        assertThat(r.days()).isOne();
    }

    @Test
    void approve_로_APPROVED_전이() {
        LeaveRequest r = LeaveRequest.request(1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), "x");

        r.approve();

        assertThat(r.getStatus()).isEqualTo(LeaveRequest.Status.APPROVED);
    }

    @Test
    void reject_로_REJECTED_전이() {
        LeaveRequest r = LeaveRequest.request(1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), "x");

        r.reject();

        assertThat(r.getStatus()).isEqualTo(LeaveRequest.Status.REJECTED);
    }
}