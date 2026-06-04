package com.erp.crm.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "crm_consultation")
public class Consultation extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long counselorId;
    private String subject;
    private String content;
    private LocalDateTime consultedAt;

    protected Consultation() {}

    public static Consultation record(Long customerId, Long counselorId, String subject, String content) {
        Consultation c = new Consultation();
        c.customerId = customerId;
        c.counselorId = counselorId;
        c.subject = subject;
        c.content = content;
        c.consultedAt = LocalDateTime.now();
        return c;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
}