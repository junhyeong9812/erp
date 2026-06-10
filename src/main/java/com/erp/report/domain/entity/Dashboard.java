package com.erp.report.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_dashboard")
public class Dashboard extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long ownerId;

    @ElementCollection
    @CollectionTable(name = "report_dashboard_metric", joinColumns = @JoinColumn(name = "dashboard_id"))
    @Column(name = "metric_name")
    private List<String> metricNames = new ArrayList<>();

    protected Dashboard() {}

    public static Dashboard create(String name, Long ownerId, List<String> metricNames) {
        Dashboard d = new Dashboard();
        d.name = name; d.ownerId = ownerId;
        d.metricNames = new ArrayList<>(metricNames);
        return d;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
}