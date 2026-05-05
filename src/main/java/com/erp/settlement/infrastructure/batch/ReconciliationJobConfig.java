package com.erp.settlement.infrastructure.batch;

import com.erp.common.messaging.EventBus;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.domain.entity.Ledger;
import com.erp.settlement.domain.event.LedgerUnbalancedEvent;
import com.erp.settlement.domain.service.LedgerReconciliation;
import com.erp.settlement.infrastructure.batch.port.BatchJobExecutionLogRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.time.LocalDate;

@Configuration
public class ReconciliationJobConfig {

    @Bean
    public Job reconciliationJob(JobRepository jr, Step reconStep,
                                 BatchJobExecutionLogRepository logRepo) {
        return new JobBuilder("reconciliationJob", jr)
                .listener(new BatchJobExecutionLogListener(logRepo))
                .start(reconStep)
                .build();
    }

    @Bean
    public Step reconStep(JobRepository jr, PlatformTransactionManager tm,
                          LedgerRepository ledgerRepo,
                          EventBus eventBus,
                          SettlementPeriodRepository periodRepo) {
        return new StepBuilder("reconStep", jr)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate today = LocalDate.now();
                    var period = periodRepo.findOpenContaining(today).orElse(null);
                    if (period == null) return RepeatStatus.FINISHED;
                    var ledgers = ledgerRepo.findByPeriodId(period.getId());
                    var result = LedgerReconciliation.verify(ledgers);
                    if (!result.balanced()) {
                        eventBus.publish(new LedgerUnbalancedEvent(
                                period.getId(),
                                result.unbalancedRefs().stream().map(Ledger::getId).toList(),
                                Instant.now()));
                    }
                    for (int i = 0; i < ledgers.size(); i++) contribution.incrementReadCount();
                    return RepeatStatus.FINISHED;
                }, tm)
                .build();
    }
}
