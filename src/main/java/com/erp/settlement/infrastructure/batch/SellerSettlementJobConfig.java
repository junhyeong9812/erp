package com.erp.settlement.infrastructure.batch;

import com.erp.common.exception.ConflictException;
import com.erp.settlement.application.port.inbound.SellerSettlementUseCase;
import com.erp.settlement.infrastructure.batch.port.BatchJobExecutionLogRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SellerSettlementJobConfig {

    @Bean
    public Job sellerSettlementJob(JobRepository jr, Step sellerStep,
                                   BatchJobExecutionLogRepository logRepo) {
        return new JobBuilder("sellerSettlementJob", jr)
                .listener(new BatchJobExecutionLogListener(logRepo))
                .start(sellerStep)
                .build();
    }

    @Bean
    @StepScope
    public Step sellerStep(JobRepository jr, PlatformTransactionManager tm,
                           SellerSettlementUseCase useCase,
                           ActiveSellerReader reader,
                           @Value("#{jobParameters['periodId']}") Long periodId) {
        return new StepBuilder("sellerStep", jr)
                .<Long, Long>chunk(1000, tm)
                .reader(reader)
                .processor(sellerId -> {
                    useCase.calculate(sellerId, periodId);
                    return sellerId;
                })
                .writer(items -> {})
                .faultTolerant().skipLimit(50).skip(ConflictException.class)
                .build();
    }
}
