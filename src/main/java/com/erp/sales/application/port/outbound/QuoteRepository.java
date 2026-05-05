package com.erp.sales.application.port.outbound;

import com.erp.sales.domain.entity.Quote;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository {
    Quote save(Quote quote);
    Optional<Quote> findById(Long id);
    List<Quote> findActiveExpirableQuotes();
}
