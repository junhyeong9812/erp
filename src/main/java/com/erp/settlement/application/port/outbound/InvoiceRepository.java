package com.erp.settlement.application.port.outbound;

import com.erp.settlement.domain.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(Long id);
    List<Invoice> findOutstanding();
}
