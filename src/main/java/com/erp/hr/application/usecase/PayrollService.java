package com.erp.hr.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.hr.application.dto.command.CalculatePayrollCommand;
import com.erp.hr.application.port.inbound.PayrollUseCase;
import com.erp.hr.application.port.outbound.*;
import com.erp.hr.domain.entity.Employee;
import com.erp.hr.domain.entity.Payroll;
import com.erp.hr.domain.entity.WorkSchedule;
import com.erp.hr.domain.service.AllowanceCalculator;
import com.erp.hr.domain.service.WorkTimeCalculationPolicy;
import com.erp.hr.domain.vo.AllowanceBreakdown;
import com.erp.hr.domain.vo.WorkTimeBreakdown;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PayrollService implements PayrollUseCase {

    private final EmployeeRepository employeeRepo;
    private final AttendanceRepository attendanceRepo;
    private final WorkScheduleRepository scheduleRepo;
    private final PayrollRepository payrollRepo;
    private final WorkTimeCalculationPolicy workTimePolicy;
    private final AllowanceCalculator allowanceCalculator;
    private final HolidayCalendar holidayCalendar;
    private final EventBus eventBus;

    public PayrollService(EmployeeRepository employeeRepo, AttendanceRepository attendanceRepo,
                          WorkScheduleRepository scheduleRepo, PayrollRepository payrollRepo,
                          WorkTimeCalculationPolicy workTimePolicy, AllowanceCalculator allowanceCalculator,
                          HolidayCalendar holidayCalendar, EventBus eventBus) {
        this.employeeRepo = employeeRepo;
        this.attendanceRepo = attendanceRepo;
        this.scheduleRepo = scheduleRepo;
        this.payrollRepo = payrollRepo;
        this.workTimePolicy = workTimePolicy;
        this.allowanceCalculator = allowanceCalculator;
        this.holidayCalendar = holidayCalendar;
        this.eventBus = eventBus;
    }

    @Override
    public Long calculate(CalculatePayrollCommand cmd) {
        YearMonth period = YearMonth.of(cmd.year(), cmd.month());

        // 재계산 가드 — 이미 산출된 급여면 멱등하게 기존 ID 반환(중복 급여/이벤트 방지).
        //         강제 재산정(RECALCULATE)은 Payroll 에 status(DRAFT/CONFIRMED) 도입 후 확장.
        Optional<Payroll> existing = payrollRepo.findByEmployeeAndPeriod(cmd.employeeId(), period.toString());
        if (existing.isPresent()) return existing.get().getId();

        // 기준급여는 cmd가 아니라 Employee에서 읽어 Payroll에 스냅샷.
        Employee emp = employeeRepo.findById(cmd.employeeId()).orElseThrow();
        Money baseSalary = emp.getBaseSalary();

        WorkSchedule schedule = scheduleRepo.findByEmployee(cmd.employeeId()).orElseThrow();

        // 근태 → 일별 WorkTimeBreakdown (checkOut 없는 기록은 toWorkPeriod()가 빈 값 → 제외)
        List<WorkTimeBreakdown> daily = attendanceRepo.findByEmployeeAndMonth(cmd.employeeId(), period).stream()
                .flatMap(a -> a.toWorkPeriod().stream()
                        .map(wp -> workTimePolicy.calculate(wp, schedule,
                                holidayCalendar.isHoliday(a.getCheckIn().toLocalDate()))))
                .toList();

        // 월 합산은 서비스 for문이 아니라 도메인 팩토리.
        WorkTimeBreakdown monthly = WorkTimeBreakdown.sum(daily);

        AllowanceBreakdown allowance = allowanceCalculator.calculate(baseSalary, monthly);

        Payroll p = Payroll.calculate(cmd.employeeId(), period, baseSalary, allowance, cmd.insuranceRate());
        p.assignId(IdGenerator.next());
        payrollRepo.save(p);
        eventBus.publishAll(p.pullEvents());
        return p.getId();
    }
}