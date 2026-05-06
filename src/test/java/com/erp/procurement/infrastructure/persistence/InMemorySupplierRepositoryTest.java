package com.erp.procurement.infrastructure.persistence;

import com.erp.procurement.domain.entity.Supplier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemorySupplierRepositoryTest {

    @Test
    void save_후_findById_로_조회() {
        InMemorySupplierRepository repo = new InMemorySupplierRepository();
        Supplier s = Supplier.register("SUP-001", "알파상사", "02-0000-0000");
        s.assignId(1L);

        repo.save(s);

        assertThat(repo.findById(1L)).isPresent()
                .get().extracting(Supplier::getCode).isEqualTo("SUP-001");
    }

    @Test
    void 없는_id_는_empty() {
        InMemorySupplierRepository repo = new InMemorySupplierRepository();

        assertThat(repo.findById(999L)).isEmpty();
    }
}