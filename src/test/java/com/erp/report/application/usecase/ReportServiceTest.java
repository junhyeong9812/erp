package com.erp.report.application.usecase;

import com.erp.common.domain.DomainEvent;
import com.erp.common.messaging.EventBus;
import com.erp.report.application.dto.command.GenerateReportCommand;
import com.erp.report.infrastructure.persistence.InMemoryReportSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReportServiceTest {

    static class FakeEventBus implements EventBus {
        final List<DomainEvent> published = new ArrayList<>();
        @Override public void publish(DomainEvent event) { published.add(event); }
        @Override public void publishAll(List<? extends DomainEvent> events) { published.addAll(events); }
        @Override public void publishAll(Iterable<? extends DomainEvent> events) { events.forEach(published::add); }
    }

    private InMemoryReportSnapshotRepository repo;
    private FakeEventBus bus;
    private ReportService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryReportSnapshotRepository();
        bus = new FakeEventBus();
        service = new ReportService(repo, bus);
    }

    @Test
    void generate_하면_Snapshot_저장되고_id_반환() {
        Long id = service.generate(new GenerateReportCommand(
                "DAILY_SALES", LocalDate.of(2026, 4, 20),
                Map.of("total", 500.0)));

        assertThat(id).isNotNull();
        assertThat(repo.findById(id)).isPresent();
        assertThat(repo.findById(id).get().metric("total")).isEqualTo(500.0);
    }

    @Test
    void generate_하면_ReportGeneratedEvent_발행() {
        service.generate(new GenerateReportCommand(
                "DAILY_SALES", LocalDate.now(), Map.of("a", 1.0)));

        assertThat(bus.published).hasSize(1);
        assertThat(bus.published.get(0).getClass().getSimpleName())
                .isEqualTo("ReportGeneratedEvent");
    }

    @Test
    void generate_후_pullEvents_는_비워져_중복_발행_안_됨() {
        Long id = service.generate(new GenerateReportCommand(
                "X", LocalDate.now(), Map.of()));

        // 서비스가 pullEvents 로 꺼냈으므로 이후 events() 는 비어 있어야 함
        assertThat(repo.findById(id).get().events()).isEmpty();
    }
}