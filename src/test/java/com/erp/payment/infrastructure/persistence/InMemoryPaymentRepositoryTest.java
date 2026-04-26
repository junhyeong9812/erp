package com.erp.payment.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.common.support.IdGenerator;
import com.erp.payment.domain.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPaymentRepositoryTest {

    private InMemoryPaymentRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryPaymentRepository(); }

    private Payment newPayment(Long orderId, long amount) {
        Payment p = Payment.request(orderId, Payment.Method.CARD, Money.of(amount));
        p.assignId(IdGenerator.next());
        return p;
    }

    @Test
    void save_후_findById_조회() {
        Payment p = newPayment(1L, 1000);
        repo.save(p);

        assertThat(repo.findById(p.getId())).isPresent().get().isSameAs(p);
    }

    @Test
    void findByOrderId_는_orderId_일치_복수_반환() {
        repo.save(newPayment(1L, 500));
        repo.save(newPayment(1L, 500));
        repo.save(newPayment(2L, 1000));

        assertThat(repo.findByOrderId(1L)).hasSize(2);
        assertThat(repo.findByOrderId(2L)).hasSize(1);
        assertThat(repo.findByOrderId(3L)).isEmpty();
    }
}