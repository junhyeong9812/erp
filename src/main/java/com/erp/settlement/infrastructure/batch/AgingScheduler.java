package com.erp.settlement.infrastructure.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AgingScheduler {

    private final JobLauncher jobLauncher;
    private final Job agingJob;

    public AgingScheduler(JobLauncher jobLauncher, Job agingJob) {
        this.jobLauncher = jobLauncher;
        this.agingJob = agingJob;
    }

    @Scheduled(cron = "0 30 1 * * *")
    public void run() throws Exception {
        jobLauncher.run(agingJob, new JobParametersBuilder()
                .addString("runDate", LocalDate.now().toString())
                .toJobParameters());
    }
}
