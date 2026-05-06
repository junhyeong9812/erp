package com.erp.procurement.infrastructure.persistence;

import com.erp.procurement.domain.entity.ReorderPolicy;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryReorderPolicyRepositoryTest {

    @Test
    void save_후_findByProductId_로_조회() {
        InMemoryReorderPolicyRepository repo = new InMemoryReorderPolicyRepository();
        ReorderPolicy p = ReorderPolicy.of(100L, 7L, 200);
        p.assignId(1L);

        repo.save(p);

        assertThat(repo.findByProductId(100L)).isPresent()
                .get().extracting(ReorderPolicy::getDefaultSupplierId).isEqualTo(7L);
    }

    @Test
    void 정책_없는_productId_는_empty() {
        InMemoryReorderPolicyRepository repo = new InMemoryReorderPolicyRepository();

        assertThat(repo.findByProductId(999L)).isEmpty();
    }
}