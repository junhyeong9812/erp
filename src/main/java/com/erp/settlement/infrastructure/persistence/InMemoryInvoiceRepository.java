package com.erp.settlement.infrastructure.persistence;

import com.erp.settlement.application.port.outbound.InvoiceRepository;
import com.erp.settlement.domain.entity.Invoice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryInvoiceRepository implements InvoiceRepository {

    private final Map<Long, Invoice> store = new ConcurrentHashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        store.put(invoice.getId(), invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Invoice> findOutstanding() {
        return store.values().stream()
                .filter(i -> i.getStatus() == Invoice.Status.OUTSTANDING)
                .toList();
    }
}
