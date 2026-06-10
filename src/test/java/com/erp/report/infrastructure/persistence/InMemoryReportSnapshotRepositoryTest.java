package com.erp.report.infrastructure.persistence;

import com.erp.common.support.IdGenerator;
import com.erp.report.domain.entity.ReportSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryReportSnapshotRepositoryTest {

    private InMemoryReportSnapshotRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryReportSnapshotRepository(); }

    @Test
    void findByTypeAndDate_로_조회() {
        ReportSnapshot s = ReportSnapshot.generate(
                "DAILY_SALES", LocalDate.of(2026, 4, 20), Map.of("total", 100.0));
        s.assignId(IdGenerator.next());
        repo.save(s);

        var found = repo.findByTypeAndDate("DAILY_SALES", LocalDate.of(2026, 4, 20));
        assertThat(found).isPresent();
        assertThat(found.get().metric("total")).isEqualTo(100.0);
    }

    @Test
    void 다른_type_또는_date_는_empty() {
        ReportSnapshot s = ReportSnapshot.generate(
                "DAILY_SALES", LocalDate.of(2026, 4, 20), Map.of());
        s.assignId(IdGenerator.next());
        repo.save(s);

        assertThat(repo.findByTypeAndDate("WEEKLY_SALES", LocalDate.of(2026, 4, 20))).isEmpty();
        assertThat(repo.findByTypeAndDate("DAILY_SALES", LocalDate.of(2026, 4, 21))).isEmpty();
    }
}