package com.erp.production.infrastructure.persistence;

import com.erp.production.domain.entity.WorkOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryWorkOrderRepositoryTest {

    InMemoryWorkOrderRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryWorkOrderRepository(); }

    @Test
    void save_후_findById_로_조회() {
        WorkOrder wo = WorkOrder.issue(100L, 50);
        wo.assignId(1L);

        repo.save(wo);

        assertThat(repo.findById(1L)).isPresent()
                .get().extracting(WorkOrder::getProductId).isEqualTo(100L);
    }

    @Test
    void findById_없는_id_는_empty() {
        assertThat(repo.findById(404L)).isEmpty();
    }
}