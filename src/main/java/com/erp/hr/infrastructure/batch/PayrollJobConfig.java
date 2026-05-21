package com.erp.hr.infrastructure.batch;

import com.erp.hr.application.dto.command.CalculatePayrollCommand;
import com.erp.hr.application.port.inbound.PayrollUseCase;
import com.erp.hr.application.port.outbound.EmployeeRepository;
import com.erp.hr.application.port.outbound.PaymentGateway;
import com.erp.hr.application.port.outbound.PaymentGateway.PayoutRequest;
import com.erp.hr.application.port.outbound.PayrollRepository;
import com.erp.hr.domain.entity.Employee;
import com.erp.hr.domain.entity.Payroll;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.YearMonth;

@Configuration
public class PayrollJobConfig {

    private static final double DEFAULT_INSURANCE_RATE = 0.09; // 4대보험 단순 요율(stub)

    @Bean
    public Job payrollJob(JobRepository jr, Step payrollStep) {
        // (codex) JobParameters['period'] 를 identifying 으로 → 동일 period 재실행은 같은 JobInstance restart.
        //         강제 재산정은 runType=RECALCULATE 같은 명시 파라미터 + 급여/입금 상태검증으로 확장.
        return new JobBuilder("payrollJob", jr).start(payrollStep).build();
    }

    @Bean
    @JobScope   // period 늦은 바인딩(@Value jobParameters)을 위해 JobScope
    public Step payrollStep(JobRepository jr, PlatformTransactionManager tm,
                            EmployeeRepository employeeRepo,
                            PayrollUseCase payrollUseCase,
                            PayrollRepository payrollRepo,
                            PaymentGateway paymentGateway,
                            @Value("#{jobParameters['period']}") String period) {
        YearMonth ym = YearMonth.parse(period); // 예: "2026-04"
        return new StepBuilder("payrollStep", jr)
                .<Employee, Long>chunk(50, tm)
                .reader(new IteratorItemReader<>(employeeRepo.findActive().iterator()))
                // processor: 직원 → 급여 산출(서비스가 재계산 가드로 멱등). 산출된 payrollId 반환.
                .processor(emp -> payrollUseCase.calculate(
                        new CalculatePayrollCommand(emp.getId(), ym.getYear(), ym.getMonthValue(), DEFAULT_INSURANCE_RATE)))
                // writer: 입금(모킹). idempotencyKey 로 재시작 시 중복 입금 차단(codex).
                .writer(payrollIds -> {
                    for (Long id : payrollIds) {
                        Payroll p = payrollRepo.findById(id).orElseThrow();
                        paymentGateway.deposit(new PayoutRequest(
                                p.getId(), p.getEmployeeId(), p.getPeriod(), p.getNetSalary(),
                                "payout-" + p.getEmployeeId() + "-" + p.getPeriod()));
                    }
                })
                .build();
    }
}