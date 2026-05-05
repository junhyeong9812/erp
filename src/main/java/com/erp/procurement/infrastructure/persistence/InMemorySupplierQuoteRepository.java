package com.erp.procurement.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.procurement.application.port.outbound.SupplierQuoteRepository;
import com.erp.procurement.domain.entity.SupplierQuote;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

@Repository
public class InMemorySupplierQuoteRepository extends InMemoryRepository<SupplierQuote, Long>
        implements SupplierQuoteRepository {
    @Override protected Long extractId(SupplierQuote q) { return q.getId(); }

    @Override
    public Optional<SupplierQuote> findLatestByProductAndSupplier(Long productId, Long supplierId) {
        return findAllBy(q ->
                q.getProductId().equals(productId) && q.getSupplierId().equals(supplierId))
                .stream()
                .max(Comparator.comparing(
                        q -> q.getCreatedAt() == null ? LocalDateTime.MIN : q.getCreatedAt()));
    }
}
