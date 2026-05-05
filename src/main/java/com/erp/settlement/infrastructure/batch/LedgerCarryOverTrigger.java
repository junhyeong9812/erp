package com.erp.settlement.infrastructure.batch;

import com.erp.settlement.domain.event.PeriodClosedEvent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LedgerCarryOverTrigger {

    private final JobLauncher jobLauncher;
    private final Job ledgerCarryOverJob;

    public LedgerCarryOverTrigger(JobLauncher jobLauncher, Job ledgerCarryOverJob) {
        this.jobLauncher = jobLauncher;
        this.ledgerCarryOverJob = ledgerCarryOverJob;
    }

    @ApplicationModuleListener
    public void on(PeriodClosedEvent event) throws Exception {
        jobLauncher.run(ledgerCarryOverJob, new JobParametersBuilder()
                .addLong("closedPeriodId", event.periodId())
                .addString("runDate", LocalDate.now().toString())
                .toJobParameters());
    }
}
