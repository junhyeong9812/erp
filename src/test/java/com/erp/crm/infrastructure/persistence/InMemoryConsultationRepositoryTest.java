package com.erp.crm.infrastructure.persistence;

import com.erp.crm.domain.entity.Consultation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryConsultationRepositoryTest {

    private InMemoryConsultationRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryConsultationRepository(); }

    @Test
    void save_후_findById_로_조회() {
        Consultation c = Consultation.record(1L, 10L, "문의", "내용");
        c.assignId(1L);

        repo.save(c);

        assertThat(repo.findById(1L)).isPresent();
    }

    @Test
    void count_는_저장된_갯수() {
        Consultation a = Consultation.record(1L, 10L, "a", "a");
        a.assignId(1L);
        Consultation b = Consultation.record(1L, 10L, "b", "b");
        b.assignId(2L);
        repo.save(a);
        repo.save(b);

        assertThat(repo.count()).isEqualTo(2);
    }
}