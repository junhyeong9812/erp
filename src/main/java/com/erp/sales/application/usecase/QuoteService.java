package com.erp.sales.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.sales.application.dto.command.CreateQuoteCommand;
import com.erp.sales.application.port.inbound.QuoteUseCase;
import com.erp.sales.application.port.outbound.QuoteRepository;
import com.erp.sales.domain.entity.Quote;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuoteService implements QuoteUseCase {

    private final QuoteRepository quoteRepository;
    private final EventBus eventBus;

    public QuoteService(QuoteRepository quoteRepository, EventBus eventBus) {
        this.quoteRepository = quoteRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Long createQuote(CreateQuoteCommand cmd) {
        long total = cmd.lines().stream().mapToLong(l -> l.unitPrice() * l.quantity()).sum();
        Quote quote = Quote.issue(cmd.customerId(), Money.of(total), cmd.validUntil());
        quote.assignId(IdGenerator.next());
        quoteRepository.save(quote);
        return quote.getId();
    }

    @Override
    public void acceptQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(NotFoundException::new);
        quote.accept();
        quoteRepository.save(quote);
        eventBus.publishAll(quote.pullEvents());
    }

    @Override
    public void expireQuote(Long quoteId) {
        Quote quote = quoteRepository.findById(quoteId).orElseThrow(NotFoundException::new);
        quote.expire();
        quoteRepository.save(quote);
        eventBus.publishAll(quote.pullEvents());
    }
}