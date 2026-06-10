package com.erp.report.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.report.domain.event.ReportGeneratedEvent;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "report_snapshot")
public class ReportSnapshot extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reportType;
    private LocalDate targetDate;

    @ElementCollection
    @CollectionTable(name = "report_snapshot_metric", joinColumns = @JoinColumn(name = "snapshot_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, Double> metrics = new HashMap<>();

    protected ReportSnapshot() {}

    public static ReportSnapshot generate(String reportType, LocalDate targetDate, Map<String, Double> metrics) {
        ReportSnapshot s = new ReportSnapshot();
        s.reportType = reportType;
        s.targetDate = targetDate;
        s.metrics = new HashMap<>(metrics);
        s.register(new ReportGeneratedEvent(null, reportType, targetDate, Instant.now()));
        return s;
    }

    public double metric(String name) { return metrics.getOrDefault(name, 0.0); }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public String getReportType() { return reportType; }
    public LocalDate getTargetDate() { return targetDate; }
    public Map<String, Double> getMetrics() { return metrics; }
}