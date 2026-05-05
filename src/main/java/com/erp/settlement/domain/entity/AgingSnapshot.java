package com.erp.settlement.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "aging_snapshot")
public class AgingSnapshot extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate snapshotDate;
    private String bucket;
    private long outstandingAmount;
    private int invoiceCount;

    protected AgingSnapshot() {}

    public static AgingSnapshot of(LocalDate snapshotDate, String bucket,
                                   long outstandingAmount, int invoiceCount) {
        AgingSnapshot s = new AgingSnapshot();
        s.snapshotDate = snapshotDate;
        s.bucket = bucket;
        s.outstandingAmount = outstandingAmount;
        s.invoiceCount = invoiceCount;
        return s;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public LocalDate getSnapshotDate() { return snapshotDate; }
    public String getBucket() { return bucket; }
    public long getOutstandingAmount() { return outstandingAmount; }
    public int getInvoiceCount() { return invoiceCount; }
}
