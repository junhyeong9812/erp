package com.erp.settlement.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_job_execution_log")
public class BatchJobExecutionLog extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobExecutionId;   // Spring Batch 의 JobExecution.id
    private String jobName;

    @Column(length = 1000)
    private String parameters;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    private int readCount;
    private int writeCount;
    private int skipCount;

    @Column(length = 2000)
    private String failureMessage;

    protected BatchJobExecutionLog() {}

    public static BatchJobExecutionLog start(Long jobExecutionId, String jobName, String parameters) {
        BatchJobExecutionLog l = new BatchJobExecutionLog();
        l.jobExecutionId = jobExecutionId;
        l.jobName = jobName;
        l.parameters = parameters;
        l.startedAt = LocalDateTime.now();
        l.status = Status.STARTED;
        return l;
    }

    public void finish(Status status, int read, int write, int skip, String failure) {
        this.status = status;
        this.readCount = read;
        this.writeCount = write;
        this.skipCount = skip;
        this.failureMessage = failure;
        this.endedAt = LocalDateTime.now();
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getJobExecutionId() { return jobExecutionId; }
    public Status getStatus() { return status; }

    public enum Status { STARTED, COMPLETED, FAILED }
}