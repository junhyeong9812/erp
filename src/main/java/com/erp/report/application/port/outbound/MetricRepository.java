package com.erp.report.application.port.outbound;

import com.erp.report.domain.entity.Metric;

import java.util.List;

public interface MetricRepository {
    Metric save(Metric metric);
    List<Metric> findByName(String name);
}