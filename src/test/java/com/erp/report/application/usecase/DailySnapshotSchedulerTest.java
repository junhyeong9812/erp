package com.erp.report.application.usecase;

import com.erp.common.domain.DomainEvent;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.report.domain.entity.Metric;
import com.erp.report.infrastructure.persistence.InMemoryMetricRepository;
import com.erp.report.infrastructure.persistence.InMemoryReportSnapshotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DailySnapshotSchedulerTest {

    static class NoopEventBus implements EventBus {
        @Override public void publish(DomainEvent event) {}
        @Override public void publishAll(List<? extends DomainEvent> events) {}
        @Override public void publishAll(Iterable<? extends DomainEvent> events) {}
    }

    private InMemoryMetricRepository metricRepo;
    private InMemoryReportSnapshotRepository snapshotRepo;
    private ReportService reportService;
    private DailySnapshotScheduler scheduler;

    @BeforeEach
    void setUp() {
        metricRepo = new InMemoryMetricRepository();
        snapshotRepo = new InMemoryReportSnapshotRepository();
        reportService = new ReportService(snapshotRepo, new NoopEventBus());
        scheduler = new DailySnapshotScheduler(reportService, metricRepo);
    }

    @Test
    void generate_는_누적_payment_amount_를_합산하여_스냅샷_생성() {
        saveMetric("payment.amount", "order:1", 1000.0);
        saveMetric("payment.amount", "order:2", 2500.0);
        saveMetric("sales.order.quantity", "customer:1", 3.0);
        saveMetric("sales.order.quantity", "customer:2", 7.0);

        scheduler.generate();

        var found = snapshotRepo.findByTypeAndDate(
                "DAILY_SALES", LocalDate.now().minusDays(1));
        assertThat(found).isPresent();
        assertThat(found.get().metric("total_payment")).isEqualTo(3500.0);
        assertThat(found.get().metric("total_quantity")).isEqualTo(10.0);
    }

    @Test
    void Metric_이_없어도_스냅샷은_0_값으로_생성() {
        scheduler.generate();

        var found = snapshotRepo.findByTypeAndDate(
                "DAILY_SALES", LocalDate.now().minusDays(1));
        assertThat(found).isPresent();
        assertThat(found.get().metric("total_payment")).isZero();
        assertThat(found.get().metric("total_quantity")).isZero();
    }

    @Test
    void 관련없는_Metric_이름은_집계에_포함되지_않음() {
        saveMetric("payment.amount", "o:1", 100.0);
        saveMetric("unrelated.metric", "x", 9999.0);

        scheduler.generate();

        var found = snapshotRepo.findByTypeAndDate(
                "DAILY_SALES", LocalDate.now().minusDays(1));
        assertThat(found.get().metric("total_payment")).isEqualTo(100.0);
        // unrelated 는 어디에도 반영되면 안 됨
        assertThat(found.get().getMetrics().values()).doesNotContain(9999.0);
    }

    private void saveMetric(String name, String dim, double value) {
        Metric m = Metric.of(name, dim, value);
        m.assignId(IdGenerator.next());
        metricRepo.save(m);
    }
}