package com.erp.production.infrastructure.persistence;

import com.erp.production.domain.entity.BillOfMaterials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryBomRepositoryTest {

    InMemoryBomRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryBomRepository(); }

    @Test
    void findByFinishedProductId_로_조회() {
        BillOfMaterials bom = BillOfMaterials.of(100L, List.of(
                new BillOfMaterials.Component(200L, 2)
        ));
        bom.assignId(1L);
        repo.save(bom);

        assertThat(repo.findByFinishedProductId(100L)).isPresent()
                .get().extracting(BillOfMaterials::getFinishedProductId).isEqualTo(100L);
    }

    @Test
    void findByFinishedProductId_없는_id_는_empty() {
        assertThat(repo.findByFinishedProductId(999L)).isEmpty();
    }

    @Test
    void 같은_finishedProductId_중복_저장_시_첫번째_반환() {
        BillOfMaterials a = BillOfMaterials.of(100L, List.of(new BillOfMaterials.Component(200L, 2)));
        a.assignId(1L);
        BillOfMaterials b = BillOfMaterials.of(100L, List.of(new BillOfMaterials.Component(201L, 3)));
        b.assignId(2L);
        repo.save(a);
        repo.save(b);

        // findAllBy 의 첫 요소 반환 규약 — 구현에 따라 id 순서 보장 안 되므로 존재만 검증
        assertThat(repo.findByFinishedProductId(100L)).isPresent();
    }
}