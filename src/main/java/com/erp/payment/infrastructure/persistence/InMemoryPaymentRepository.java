package com.erp.payment.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.payment.application.port.outbound.PaymentRepository;
import com.erp.payment.domain.entity.Payment;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryPaymentRepository
        extends InMemoryRepository<Payment, Long>
        implements PaymentRepository {

    @Override
    protected Long extractId(Payment entity) { return entity.getId(); }

    @Override
    public List<Payment> findByOrderId(Long orderId) {
        return findAllBy(p -> p.getOrderId().equals(orderId));
    }
}