package com.erp.logistics.infrastructure.persistence;

import com.erp.logistics.domain.entity.Shipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryShipmentRepositoryTest {

    InMemoryShipmentRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryShipmentRepository(); }

    @Test
    void save_후_findById_로_조회() {
        Shipment s = Shipment.instruct(1L, 10L);
        s.assignId(100L);

        repo.save(s);

        assertThat(repo.findById(100L)).isPresent()
                .get().extracting(Shipment::getOrderId).isEqualTo(1L);
    }

    @Test
    void findByOrderId_는_orderId_로_조회() {
        Shipment a = Shipment.instruct(1L, 10L); a.assignId(100L);
        Shipment b = Shipment.instruct(2L, 10L); b.assignId(101L);
        repo.save(a);
        repo.save(b);

        assertThat(repo.findByOrderId(2L)).isPresent()
                .get().extracting(Shipment::getId).isEqualTo(101L);
    }

    @Test
    void findByOrderId_없는_orderId_는_empty() {
        assertThat(repo.findByOrderId(999L)).isEmpty();
    }
}