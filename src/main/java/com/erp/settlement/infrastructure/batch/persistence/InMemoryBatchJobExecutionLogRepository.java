package com.erp.settlement.infrastructure.batch.persistence;

import com.erp.settlement.domain.entity.BatchJobExecutionLog;
import com.erp.settlement.infrastructure.batch.port.BatchJobExecutionLogRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBatchJobExecutionLogRepository implements BatchJobExecutionLogRepository {

    private final Map<Long, BatchJobExecutionLog> store = new ConcurrentHashMap<>();

    @Override
    public BatchJobExecutionLog save(BatchJobExecutionLog log) {
        store.put(log.getId(), log);
        return log;
    }

    @Override
    public Optional<BatchJobExecutionLog> findByJobExecutionId(Long jobExecutionId) {
        return store.values().stream()
                .filter(l -> jobExecutionId.equals(l.getJobExecutionId()))
                .findFirst();
    }

    @Override
    public List<BatchJobExecutionLog> findRecent(int limit) {
        return store.values().stream()
                .sorted(Comparator.comparing(BatchJobExecutionLog::getId).reversed())
                .limit(limit)
                .toList();
    }
}
