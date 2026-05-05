package com.erp.procurement.application.port.outbound;

import com.erp.procurement.domain.entity.SupplierQuote;

import java.util.Optional;

public interface SupplierQuoteRepository {
    SupplierQuote save(SupplierQuote quote);
    Optional<SupplierQuote> findLatestByProductAndSupplier(Long productId, Long supplierId);
}
