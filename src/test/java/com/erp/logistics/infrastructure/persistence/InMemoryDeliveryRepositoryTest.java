package com.erp.logistics.infrastructure.persistence;

import com.erp.logistics.domain.entity.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryDeliveryRepositoryTest {

    InMemoryDeliveryRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryDeliveryRepository(); }

    @Test
    void save_후_findById_로_조회() {
        Delivery d = Delivery.assign(100L, "driver-1", "TRK-001");
        d.assignId(500L);

        repo.save(d);

        assertThat(repo.findById(500L)).isPresent()
                .get().extracting(Delivery::getShipmentId).isEqualTo(100L);
    }

    @Test
    void findById_없는_id_는_empty() {
        assertThat(repo.findById(404L)).isEmpty();
    }
}