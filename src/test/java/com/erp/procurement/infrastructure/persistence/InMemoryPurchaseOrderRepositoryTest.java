package com.erp.procurement.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.procurement.domain.entity.PurchaseOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPurchaseOrderRepositoryTest {

    private InMemoryPurchaseOrderRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryPurchaseOrderRepository(); }

    @Test
    void save_후_findById() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po.assignId(1L);

        repo.save(po);

        assertThat(repo.findById(1L)).isPresent()
                .get().extracting(PurchaseOrder::getProductId).isEqualTo(100L);
    }

    @Test
    void findByProduct_는_productId_로_필터링() {
        PurchaseOrder po1 = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po1.assignId(1L);
        PurchaseOrder po2 = PurchaseOrder.issue(1L, 200L, 30, Money.of(500));
        po2.assignId(2L);
        PurchaseOrder po3 = PurchaseOrder.issue(2L, 100L, 20, Money.of(1100));
        po3.assignId(3L);

        repo.save(po1);
        repo.save(po2);
        repo.save(po3);

        assertThat(repo.findByProduct(100L))
                .extracting(PurchaseOrder::getId)
                .containsExactlyInAnyOrder(1L, 3L);
        assertThat(repo.findByProduct(999L)).isEmpty();
    }
}