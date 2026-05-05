package com.erp.settlement.infrastructure.batch;

import com.erp.settlement.domain.event.PeriodClosedEvent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SellerSettlementTrigger {

    private final JobLauncher jobLauncher;
    private final Job sellerSettlementJob;

    public SellerSettlementTrigger(JobLauncher jobLauncher, Job sellerSettlementJob) {
        this.jobLauncher = jobLauncher;
        this.sellerSettlementJob = sellerSettlementJob;
    }

    @ApplicationModuleListener
    public void on(PeriodClosedEvent event) throws Exception {
        jobLauncher.run(sellerSettlementJob, new JobParametersBuilder()
                .addLong("periodId", event.periodId())
                .addString("runDate", LocalDate.now().toString())
                .toJobParameters());
    }
}
