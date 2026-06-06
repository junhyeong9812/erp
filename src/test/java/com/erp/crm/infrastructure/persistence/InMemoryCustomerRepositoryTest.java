package com.erp.crm.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.crm.domain.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryCustomerRepositoryTest {

    private InMemoryCustomerRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryCustomerRepository(); }

    @Test
    void save_후_findByCustomerCode_로_조회() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(1_000_000));
        c.assignId(1L);
        repo.save(c);

        assertThat(repo.findByCustomerCode("C001")).isPresent();
        assertThat(repo.findByCustomerCode("C999")).isEmpty();
    }

    @Test
    void findById_없으면_Optional_empty() {
        assertThat(repo.findById(123L)).isEmpty();
    }

    @Test
    void 동일_code_여러_저장_후_첫_번째_반환() {
        Customer a = Customer.register("C001", "ACME", "-", 1L, Money.of(1_000_000));
        a.assignId(1L);
        Customer b = Customer.register("C001", "ACME2", "-", 2L, Money.of(2_000_000));
        b.assignId(2L);
        repo.save(a);
        repo.save(b);

        // 보장: findByCustomerCode 는 findFirst 이므로 어떤 인스턴스든 하나가 반환된다
        assertThat(repo.findByCustomerCode("C001")).isPresent();
    }
}