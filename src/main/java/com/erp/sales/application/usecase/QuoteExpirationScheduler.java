package com.erp.sales.application.usecase;

import com.erp.sales.application.port.inbound.QuoteUseCase;
import com.erp.sales.application.port.outbound.QuoteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class QuoteExpirationScheduler {

    private final QuoteRepository quoteRepository;
    private final QuoteUseCase quoteUseCase;

    public QuoteExpirationScheduler(QuoteRepository quoteRepository, QuoteUseCase quoteUseCase) {
        this.quoteRepository = quoteRepository;
        this.quoteUseCase = quoteUseCase;
    }

    @Scheduled(cron = "0 0 0 * * *")   // 매일 자정
    public void expireOutdatedQuotes() {
        LocalDate today = LocalDate.now();
        quoteRepository.findActiveExpirableQuotes().forEach(q -> {
            if (q.isExpired(today)) {
                quoteUseCase.expireQuote(q.getId());
            }
        });
    }
}