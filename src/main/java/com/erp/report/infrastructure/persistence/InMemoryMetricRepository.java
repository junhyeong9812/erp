package com.erp.report.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.report.application.port.outbound.MetricRepository;
import com.erp.report.domain.entity.Metric;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryMetricRepository extends InMemoryRepository<Metric, Long> implements MetricRepository {
    @Override protected Long extractId(Metric m) { return m.getId(); }
    @Override public List<Metric> findByName(String name) {
        return findAllBy(m -> m.getMetricName().equals(name));
    }
}