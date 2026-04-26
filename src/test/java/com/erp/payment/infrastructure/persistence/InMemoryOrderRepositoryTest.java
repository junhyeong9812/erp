package com.erp.payment.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.common.support.IdGenerator;
import com.erp.payment.domain.entity.Order;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryOrderRepositoryTest {

    @Test
    void save_후_findById_조회() {
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        Order o = Order.create("ORD-1", 100L, Money.of(5000));
        o.assignId(IdGenerator.next());

        repo.save(o);

        assertThat(repo.findById(o.getId())).isPresent().get().isSameAs(o);
    }

    @Test
    void 삭제_후_존재하지_않음() {
        InMemoryOrderRepository repo = new InMemoryOrderRepository();
        Order o = Order.create("ORD-1", 100L, Money.of(1000));
        o.assignId(IdGenerator.next());
        repo.save(o);

        repo.deleteById(o.getId());

        assertThat(repo.existsById(o.getId())).isFalse();
    }
}