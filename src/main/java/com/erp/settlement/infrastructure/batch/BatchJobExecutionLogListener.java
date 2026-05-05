package com.erp.settlement.infrastructure.batch;

import com.erp.common.support.IdGenerator;
import com.erp.settlement.infrastructure.batch.port.BatchJobExecutionLogRepository;
import com.erp.settlement.domain.entity.BatchJobExecutionLog;
import org.springframework.batch.core.*;

public class BatchJobExecutionLogListener implements JobExecutionListener {

    private final BatchJobExecutionLogRepository logRepo;

    public BatchJobExecutionLogListener(BatchJobExecutionLogRepository logRepo) {
        this.logRepo = logRepo;
    }

    @Override
    public void beforeJob(JobExecution je) {
        BatchJobExecutionLog log = BatchJobExecutionLog.start(
                je.getId(),
                je.getJobInstance().getJobName(),
                je.getJobParameters().toString());
        log.assignId(IdGenerator.next());
        logRepo.save(log);
    }

    @Override
    public void afterJob(JobExecution je) {
        var log = logRepo.findByJobExecutionId(je.getId())
                .orElseThrow(() -> new IllegalStateException("log not found"));
        int read  = je.getStepExecutions().stream().mapToInt(se -> (int) se.getReadCount()).sum();
        int write = je.getStepExecutions().stream().mapToInt(se -> (int) se.getWriteCount()).sum();
        int skip  = je.getStepExecutions().stream().mapToInt(se -> (int) se.getSkipCount()).sum();
        String failure = je.getAllFailureExceptions().stream()
                .findFirst().map(Throwable::getMessage).orElse(null);
        var status = je.getStatus() == BatchStatus.COMPLETED
                ? BatchJobExecutionLog.Status.COMPLETED
                : BatchJobExecutionLog.Status.FAILED;
        log.finish(status, read, write, skip, failure);
        logRepo.save(log);
    }
}