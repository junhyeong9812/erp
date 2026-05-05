package com.erp.settlement.infrastructure.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MonthlyClosingScheduler {

    private final JobLauncher jobLauncher;
    private final Job monthlyClosingJob;

    public MonthlyClosingScheduler(JobLauncher jobLauncher, Job monthlyClosingJob) {
        this.jobLauncher = jobLauncher;
        this.monthlyClosingJob = monthlyClosingJob;
    }

    @Scheduled(cron = "0 10 0 1 * *")
    public void run() throws Exception {
        jobLauncher.run(monthlyClosingJob, new JobParametersBuilder()
                .addString("runDate", LocalDate.now().toString())
                .toJobParameters());
    }
}
