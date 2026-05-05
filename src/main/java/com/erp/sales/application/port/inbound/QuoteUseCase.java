package com.erp.sales.application.port.inbound;

import com.erp.sales.application.dto.command.CreateQuoteCommand;

public interface QuoteUseCase {
    Long createQuote(CreateQuoteCommand command);
    void acceptQuote(Long quoteId);
    void expireQuote(Long quoteId);
}
