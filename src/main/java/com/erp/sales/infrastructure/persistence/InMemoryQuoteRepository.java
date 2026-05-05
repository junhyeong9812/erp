package com.erp.sales.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.sales.application.port.outbound.QuoteRepository;
import com.erp.sales.domain.entity.Quote;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryQuoteRepository extends InMemoryRepository<Quote, Long> implements QuoteRepository {
    @Override protected Long extractId(Quote q) { return q.getId(); }
    @Override public List<Quote> findActiveExpirableQuotes() {
        return findAllBy(q -> q.getStatus() == Quote.Status.ACTIVE);
    }
}

