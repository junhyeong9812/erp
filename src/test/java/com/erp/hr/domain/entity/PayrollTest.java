package com.erp.hr.domain.entity;

import com.erp.common.domain.Money;
import com.erp.hr.domain.event.PayrollCalculatedEvent;
import com.erp.hr.domain.vo.AllowanceBreakdown;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class PayrollTest {

    /** 수당 합계가 amount 가 되도록 연장수당 단일 항목으로 구성. */
    private AllowanceBreakdown 수당(long amount) {
        return new AllowanceBreakdown(Money.of(amount), Money.ZERO, Money.ZERO);
    }

    @Test
    void calculate_는_기본급_수당_공제_실수령액_을_기록() {
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(3_000_000), 수당(500_000), 0.1);

        assertThat(p.getEmployeeId()).isEqualTo(1L);
        assertThat(p.getPeriod()).isEqualTo("2026-04");
        assertThat(p.getBaseSalary()).isEqualTo(Money.of(3_000_000));
        assertThat(p.getAllowance().total()).isEqualTo(Money.of(500_000));
    }

    @Test
    void 기본_케이스_공제는_gross_곱하기_보험률_net은_gross_빼기_공제() {
        // gross = 3,000,000 + 500,000 = 3,500,000
        // deduction = round(3,500,000 × 0.1) = 350,000
        // net = 3,150,000
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(3_000_000), 수당(500_000), 0.1);

        assertThat(p.getDeduction()).isEqualTo(Money.of(350_000));
        assertThat(p.getNetSalary()).isEqualTo(Money.of(3_150_000));
    }

    @Test
    void 수당_0원이면_기본급만으로_계산() {
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(3_000_000), 수당(0), 0.1);

        assertThat(p.getAllowance().total()).isEqualTo(Money.ZERO);
        assertThat(p.getDeduction()).isEqualTo(Money.of(300_000));
        assertThat(p.getNetSalary()).isEqualTo(Money.of(2_700_000));
    }

    @Test
    void 보험률_0이면_공제_없이_gross_전액_수령() {
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(3_000_000), 수당(500_000), 0.0);

        assertThat(p.getDeduction()).isEqualTo(Money.ZERO);
        assertThat(p.getNetSalary()).isEqualTo(Money.of(3_500_000));
    }

    @Test
    void 4대보험_종합률_약_9_4_퍼센트_시나리오() {
        // gross 3,000,000 × 0.094 = 282,000
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(3_000_000), 수당(0), 0.094);

        assertThat(p.getDeduction()).isEqualTo(Money.of(282_000));
        assertThat(p.getNetSalary()).isEqualTo(Money.of(2_718_000));
    }

    @Test
    void round_경계_0_5원이_나오면_HALF_UP_으로_올림() {
        // gross = 1,000,001, rate 0.5 → 500,000.5 (double 로 정확히 표현됨) → Math.round = 500,001
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(1_000_001), 수당(0), 0.5);

        assertThat(p.getDeduction()).isEqualTo(Money.of(500_001));
    }

    @Test
    void 연말정산_상여_포함_재계산() {
        // 상여 300만 + 기본급 300만, 보험률 0.094
        // gross = 6,000,000, deduction = round(564,000) = 564,000, net = 5,436,000
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 12),
                Money.of(3_000_000), 수당(3_000_000), 0.094);

        assertThat(p.getDeduction()).isEqualTo(Money.of(564_000));
        assertThat(p.getNetSalary()).isEqualTo(Money.of(5_436_000));
    }

    @Test
    void calculate_시_PayrollCalculatedEvent_가_등록됨() {
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(3_000_000), 수당(500_000), 0.1);

        assertThat(p.events()).hasAtLeastOneElementOfType(PayrollCalculatedEvent.class);
    }

    @Test
    void pullEvents_는_한_번만_이벤트를_반환하고_비운다() {
        Payroll p = Payroll.calculate(1L, YearMonth.of(2026, 4),
                Money.of(3_000_000), 수당(0), 0.1);

        assertThat(p.pullEvents()).hasSize(1);   // PayrollCalculatedEvent
        assertThat(p.pullEvents()).isEmpty();
    }
}