package com.erp.hr.domain.entity;

import com.erp.common.domain.Money;
import com.erp.hr.domain.event.EmployeeHiredEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployeeTest {

    private Employee 신규입사() {
        return Employee.hire("E1", "홍길동", 10L,
                LocalDate.of(2026, 4, 20), Money.of(3_000_000));
    }

    @Test
    void hire_는_ACTIVE_상태로_생성되고_연차_15일_부여() {
        Employee e = 신규입사();

        assertThat(e.getEmployeeNumber()).isEqualTo("E1");
        assertThat(e.getName()).isEqualTo("홍길동");
        assertThat(e.getDepartmentId()).isEqualTo(10L);
        assertThat(e.getStatus()).isEqualTo(Employee.Status.ACTIVE);
        assertThat(e.getRemainingLeaveDays()).isEqualTo(15);
        assertThat(e.getBaseSalary()).isEqualTo(Money.of(3_000_000));
    }

    @Test
    void hire_시_EmployeeHiredEvent_가_등록됨() {
        Employee e = 신규입사();

        assertThat(e.events()).hasAtLeastOneElementOfType(EmployeeHiredEvent.class);
    }

    @Test
    void transferTo_는_부서ID_를_갱신() {
        Employee e = 신규입사();

        e.transferTo(99L);

        assertThat(e.getDepartmentId()).isEqualTo(99L);
    }

    @Test
    void terminate_는_TERMINATED_로_전이() {
        Employee e = 신규입사();

        e.terminate(LocalDate.of(2026, 12, 31));

        assertThat(e.getStatus()).isEqualTo(Employee.Status.TERMINATED);
    }

    @Test
    void consumeLeave_는_잔여일수를_차감() {
        Employee e = 신규입사();

        e.consumeLeave(3);

        assertThat(e.getRemainingLeaveDays()).isEqualTo(12);
    }

    @Test
    void consumeLeave_잔여와_정확히_같으면_0이_되고_추가_요청은_예외() {
        Employee e = 신규입사();

        e.consumeLeave(15);

        assertThat(e.getRemainingLeaveDays()).isZero();
        assertThatThrownBy(() -> e.consumeLeave(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("잔여 연차 부족");
    }

    @Test
    void consumeLeave_누적으로_초과하면_예외() {
        Employee e = 신규입사();

        e.consumeLeave(10);
        e.consumeLeave(5);

        assertThatThrownBy(() -> e.consumeLeave(1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void pullEvents_는_리스트를_비운다() {
        Employee e = 신규입사();

        var first = e.pullEvents();
        var second = e.pullEvents();

        assertThat(first).hasSize(1);
        assertThat(second).isEmpty();
    }
}