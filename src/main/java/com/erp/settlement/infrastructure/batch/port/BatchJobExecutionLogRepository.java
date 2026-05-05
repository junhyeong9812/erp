package com.erp.settlement.infrastructure.batch.port;

import com.erp.settlement.domain.entity.BatchJobExecutionLog;

import java.util.List;
import java.util.Optional;

public interface BatchJobExecutionLogRepository {
    BatchJobExecutionLog save(BatchJobExecutionLog log);
    Optional<BatchJobExecutionLog> findByJobExecutionId(Long jobExecutionId);
    List<BatchJobExecutionLog> findRecent(int limit);
}
