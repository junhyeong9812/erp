package com.erp.payment.application.port.outbound;

import com.erp.payment.domain.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findByOrderId(Long orderId);
}