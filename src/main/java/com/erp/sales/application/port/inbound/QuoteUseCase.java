package com.erp.sales.application.port.inbound;

import com.erp.sales.application.dto.command.CreateQuoteCommand;

public interface QuoteUseCase {
    Long createQuote(CreateQuoteCommand command);
    void expireQuote(Long quoteId);
}