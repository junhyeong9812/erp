package com.erp.settlement.application.port.outbound;

import com.erp.settlement.domain.entity.AgingSnapshot;

import java.time.LocalDate;
import java.util.List;

public interface AgingSnapshotRepository {
    AgingSnapshot save(AgingSnapshot snapshot);
    List<AgingSnapshot> findBySnapshotDate(LocalDate date);
}
