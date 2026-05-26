package com.erp.hr.domain.service;

import com.erp.common.domain.Money;
import com.erp.hr.domain.vo.AllowanceBreakdown;
import com.erp.hr.domain.vo.WorkTimeBreakdown;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllowanceCalculatorTest {

    private final AllowanceCalculator calculator = new AllowanceCalculator();
    private final Money baseSalary = Money.of(2_090_000);   // 시급 10,000원으로 떨어짐

    @Test
    void 연장수당은_분당시급의_50퍼센트로_환산된다() {
        // 연장 180분 → 166.6667 × 180 × 0.5 = 15,000
        WorkTimeBreakdown bt = new WorkTimeBreakdown(480, 180, 0, 0);

        AllowanceBreakdown ab = calculator.calculate(baseSalary, bt);

        assertThat(ab.getOvertimeAllowance()).isEqualTo(Money.of(15_000));
        assertThat(ab.getNightAllowance()).isEqualTo(Money.of(0));   // 야간 0분 → 0원
    }

    @Test
    void 연장_야간_중복가산_각각_별도로_합산된다() {
        // 같은 240분이 연장·야간 두 항목에 모두 가산된다
        WorkTimeBreakdown bt = new WorkTimeBreakdown(480, 240, 240, 0);

        AllowanceBreakdown ab = calculator.calculate(baseSalary, bt);

        assertThat(ab.getOvertimeAllowance()).isEqualTo(Money.of(20_000));
        assertThat(ab.getNightAllowance()).isEqualTo(Money.of(20_000));
        assertThat(ab.getHolidayAllowance()).isEqualTo(Money.of(0));
        assertThat(ab.total()).isEqualTo(Money.of(40_000));   // 중복 가산이 합산됨
    }

    @Test
    void 야간_480분은_40000원으로_환산된다() {
        WorkTimeBreakdown bt = new WorkTimeBreakdown(480, 0, 480, 0);

        AllowanceBreakdown ab = calculator.calculate(baseSalary, bt);

        assertThat(ab.getNightAllowance()).isEqualTo(Money.of(40_000));
        assertThat(ab.total()).isEqualTo(Money.of(40_000));
    }

    @Test
    void 휴일_660분은_현재구현상_일괄_50퍼센트로_55000원이다() {
        //    법정은 8h 이내 50% + 8h 초과 100% 이지만,
        //    현재 AllowanceCalculator 는 holidayMinutes 전체에 0.5 만 적용한다
        WorkTimeBreakdown bt = new WorkTimeBreakdown(0, 0, 0, 660);

        AllowanceBreakdown ab = calculator.calculate(baseSalary, bt);

        assertThat(ab.getHolidayAllowance()).isEqualTo(Money.of(55_000));  // 166.6667 × 660 × 0.5
    }
}