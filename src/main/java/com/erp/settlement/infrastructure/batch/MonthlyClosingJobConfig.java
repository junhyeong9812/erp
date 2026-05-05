package com.erp.settlement.infrastructure.batch;

import com.erp.common.exception.ConflictException;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.infrastructure.batch.port.BatchJobExecutionLogRepository;
import com.erp.settlement.domain.entity.SettlementPeriod;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

@Configuration
public class MonthlyClosingJobConfig {

    @Bean
    public Job monthlyClosingJob(JobRepository jr,
                                 Step closingStep,
                                 BatchJobExecutionLogRepository logRepo) {
        return new JobBuilder("monthlyClosingJob", jr)
                .listener(new BatchJobExecutionLogListener(logRepo))
                .start(closingStep)
                .build();
    }

    @Bean
    public Step closingStep(JobRepository jr, PlatformTransactionManager tm,
                            SettlementPeriodRepository periodRepo,
                            SettlementPeriodUseCase periodUseCase) {
        return new StepBuilder("closingStep", jr)
                .<SettlementPeriod, SettlementPeriod>chunk(50, tm)
                .reader(new IteratorItemReader<>(
                        periodRepo.findOpenEndingBefore(LocalDate.now()).iterator()))
                .processor(period -> {
                    periodUseCase.close(period.getId());
                    return period;
                })
                .writer(items -> { /* close() 내부에서 이미 저장/이벤트 — no-op */ })
                .faultTolerant()
                .skipLimit(100)
                .skip(ConflictException.class)   // LEDGER_UNBALANCED 는 skip
                .build();
    }
}