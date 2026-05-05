package com.erp.settlement.infrastructure.batch;

import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.settlement.application.port.outbound.AgingSnapshotRepository;
import com.erp.settlement.application.port.outbound.InvoiceRepository;
import com.erp.settlement.domain.entity.AgingSnapshot;
import com.erp.settlement.domain.entity.Invoice;
import com.erp.settlement.domain.event.OverdueInvoiceEvent;
import com.erp.settlement.infrastructure.batch.dto.AgingBucketed;
import com.erp.settlement.infrastructure.batch.port.BatchJobExecutionLogRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Configuration
public class AgingJobConfig {

    @Bean
    public Job agingJob(JobRepository jr, Step agingStep,
                        BatchJobExecutionLogRepository logRepo) {
        return new JobBuilder("agingJob", jr)
                .listener(new BatchJobExecutionLogListener(logRepo))
                .start(agingStep)
                .build();
    }

    @Bean
    public Step agingStep(JobRepository jr, PlatformTransactionManager tm,
                          InvoiceRepository invRepo,
                          AgingSnapshotRepository snapshotRepo,
                          EventBus eventBus) {
        return new StepBuilder("agingStep", jr)
                .<Invoice, AgingBucketed>chunk(500, tm)
                .reader(new IteratorItemReader<>(invRepo.findOutstanding().iterator()))
                .processor(inv -> {
                    long days = ChronoUnit.DAYS.between(
                            inv.getIssuedAt().toLocalDate(), LocalDate.now());
                    String bucket = days <= 30 ? "0-30"
                            : days <= 60 ? "31-60"
                            : days <= 90 ? "61-90"
                            : "90+";
                    if (days > 90) {
                        eventBus.publish(new OverdueInvoiceEvent(
                                inv.getId(), days, Instant.now()));
                    }
                    return new AgingBucketed(bucket, inv.getAmount());
                })
                .writer(items -> {
                    var grouped = items.getItems().stream()
                            .collect(Collectors.groupingBy(
                                    AgingBucketed::bucket,
                                    Collectors.reducing(0L,
                                            a -> a.amount().amount().longValueExact(),
                                            Long::sum)));
                    grouped.forEach((bucket, sum) -> {
                        AgingSnapshot snap = AgingSnapshot.of(
                                LocalDate.now(), bucket, sum, 0);
                        snap.assignId(IdGenerator.next());
                        snapshotRepo.save(snap);
                    });
                })
                .build();
    }
}
