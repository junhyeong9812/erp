package com.erp.report.infrastructure.persistence;

import com.erp.common.support.IdGenerator;
import com.erp.report.domain.entity.Metric;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryMetricRepositoryTest {

    private InMemoryMetricRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryMetricRepository(); }

    @Test
    void findByName_은_이름으로_필터() {
        save("payment.amount", "o:1", 100.0);
        save("payment.amount", "o:2", 200.0);
        save("sales.order.quantity", "c:1", 5.0);

        assertThat(repo.findByName("payment.amount")).hasSize(2);
        assertThat(repo.findByName("sales.order.quantity")).hasSize(1);
        assertThat(repo.findByName("none")).isEmpty();
    }

    private void save(String name, String dim, double value) {
        Metric m = Metric.of(name, dim, value);
        m.assignId(IdGenerator.next());
        repo.save(m);
    }
}