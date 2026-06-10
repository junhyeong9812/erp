package com.erp.report.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report_metric")
public class Metric extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metricName;
    private String dimensionKey;
    private double value;
    private LocalDateTime recordedAt;

    protected Metric() {}

    public static Metric of(String metricName, String dimensionKey, double value) {
        Metric m = new Metric();
        m.metricName = metricName;
        m.dimensionKey = dimensionKey;
        m.value = value;
        m.recordedAt = LocalDateTime.now();
        return m;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getMetricName() { return metricName; }
    public String getDimensionKey() { return dimensionKey; }
    public double getValue() { return value; }
}