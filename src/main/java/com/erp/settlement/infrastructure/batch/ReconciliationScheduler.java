package com.erp.settlement.infrastructure.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReconciliationScheduler {

    private final JobLauncher jobLauncher;
    private final Job reconciliationJob;

    public ReconciliationScheduler(JobLauncher jobLauncher, Job reconciliationJob) {
        this.jobLauncher = jobLauncher;
        this.reconciliationJob = reconciliationJob;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void run() throws Exception {
        jobLauncher.run(reconciliationJob, new JobParametersBuilder()
                .addString("runDate", LocalDate.now().toString())
                .toJobParameters());
    }
}
