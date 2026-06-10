package com.erp.report.application.port.outbound;

import com.erp.report.domain.entity.ReportSnapshot;

import java.time.LocalDate;
import java.util.Optional;

public interface ReportSnapshotRepository {
    ReportSnapshot save(ReportSnapshot snapshot);
    Optional<ReportSnapshot> findByTypeAndDate(String type, LocalDate date);
}