package com.erp.settlement.infrastructure.persistence;

import com.erp.settlement.application.port.outbound.AgingSnapshotRepository;
import com.erp.settlement.domain.entity.AgingSnapshot;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAgingSnapshotRepository implements AgingSnapshotRepository {

    private final Map<Long, AgingSnapshot> store = new ConcurrentHashMap<>();

    @Override
    public AgingSnapshot save(AgingSnapshot snapshot) {
        store.put(snapshot.getId(), snapshot);
        return snapshot;
    }

    @Override
    public List<AgingSnapshot> findBySnapshotDate(LocalDate date) {
        return store.values().stream()
                .filter(s -> date.equals(s.getSnapshotDate()))
                .toList();
    }
}
