package com.erp.report.application.usecase;

import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.report.domain.entity.Metric;
import com.erp.report.infrastructure.persistence.InMemoryMetricRepository;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CrossModuleEventCollectorTest {

    private InMemoryMetricRepository repo;
    private CrossModuleEventCollector collector;

    @BeforeEach
    void setUp() {
        repo = new InMemoryMetricRepository();
        collector = new CrossModuleEventCollector(repo);
    }

    @Test
    void PaymentCompletedEvent_를_Metric_으로_기록() {
        collector.onPayment(new PaymentCompletedEvent(
                1L, 1234L, 5000L, Instant.now()));

        List<Metric> found = repo.findByName("payment.amount");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDimensionKey()).isEqualTo("order:1234");
        assertThat(found.get(0).getValue()).isEqualTo(5000.0);
    }

    @Test
    void SalesOrderPlacedEvent_의_라인_수량_합산() {
        collector.onOrderPlaced(new SalesOrderPlacedEvent(
                10L, 7L,
                List.of(
                        new SalesOrderPlacedEvent.Line(100L, 3),
                        new SalesOrderPlacedEvent.Line(200L, 2),
                        new SalesOrderPlacedEvent.Line(300L, 5)
                ),
                Instant.now()));

        List<Metric> found = repo.findByName("sales.order.quantity");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getDimensionKey()).isEqualTo("customer:7");
        assertThat(found.get(0).getValue()).isEqualTo(10.0);   // 3+2+5
    }

    @Test
    void 여러_이벤트_누적() {
        collector.onPayment(new PaymentCompletedEvent(1L, 100L, 1000L, Instant.now()));
        collector.onPayment(new PaymentCompletedEvent(2L, 101L, 2000L, Instant.now()));
        collector.onOrderPlaced(new SalesOrderPlacedEvent(
                11L, 7L,
                List.of(new SalesOrderPlacedEvent.Line(1L, 4)),
                Instant.now()));

        assertThat(repo.findByName("payment.amount")).hasSize(2);
        assertThat(repo.findByName("sales.order.quantity")).hasSize(1);
    }

    @Test
    void 각_Metric_은_고유_id_를_가진다() {
        collector.onPayment(new PaymentCompletedEvent(1L, 1L, 100L, Instant.now()));
        collector.onPayment(new PaymentCompletedEvent(2L, 2L, 200L, Instant.now()));

        var ids = repo.findByName("payment.amount").stream().map(Metric::getId).toList();
        assertThat(ids).doesNotContainNull();
        assertThat(ids).doesNotHaveDuplicates();
    }
}