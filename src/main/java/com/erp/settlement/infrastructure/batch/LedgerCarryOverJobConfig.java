package com.erp.settlement.infrastructure.batch;

import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.infrastructure.batch.port.BatchJobExecutionLogRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LedgerCarryOverJobConfig {

    @Bean
    public Job ledgerCarryOverJob(JobRepository jr, Step carryOverStep,
                                  BatchJobExecutionLogRepository logRepo) {
        return new JobBuilder("ledgerCarryOverJob", jr)
                .listener(new BatchJobExecutionLogListener(logRepo))
                .start(carryOverStep)
                .build();
    }

    @Bean
    @StepScope
    public Step carryOverStep(JobRepository jr, PlatformTransactionManager tm,
                              LedgerRepository ledgerRepo,
                              SettlementPeriodRepository periodRepo,
                              @Value("#{jobParameters['closedPeriodId']}") Long closedPeriodId) {
        return new StepBuilder("carryOverStep", jr)
                .tasklet((contribution, ctx) -> {
                    var closed = periodRepo.findById(closedPeriodId).orElseThrow();
                    var nextOpen = periodRepo.findOpenContaining(
                            closed.getEndDate().plusDays(1)).orElse(null);
                    if (nextOpen == null) return RepeatStatus.FINISHED;

                    var candidates = ledgerRepo.findByPeriodId(closedPeriodId).stream()
                            .filter(l -> l.getCreatedAt() != null
                                    && l.getCreatedAt().isAfter(
                                    closed.getEndDate().atTime(23, 59, 0)))
                            .toList();
                    candidates.forEach(l -> l.reassignPeriod(nextOpen.getId()));
                    contribution.incrementWriteCount(candidates.size());   // takes int — OK
                    return RepeatStatus.FINISHED;
                }, tm)
                .build();
    }
}
