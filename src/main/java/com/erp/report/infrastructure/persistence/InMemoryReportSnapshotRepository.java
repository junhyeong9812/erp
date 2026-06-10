package com.erp.report.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.report.application.port.outbound.ReportSnapshotRepository;
import com.erp.report.domain.entity.ReportSnapshot;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class InMemoryReportSnapshotRepository extends InMemoryRepository<ReportSnapshot, Long>
        implements ReportSnapshotRepository {
    @Override protected Long extractId(ReportSnapshot s) { return s.getId(); }
    @Override public Optional<ReportSnapshot> findByTypeAndDate(String type, LocalDate date) {
        return findAllBy(s -> s.getReportType().equals(type) && s.getTargetDate().equals(date))
                .stream().findFirst();
    }
}