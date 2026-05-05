package com.erp.sales.application.usecase;

import com.erp.common.domain.Money;
import com.erp.sales.application.port.inbound.QuoteUseCase;
import com.erp.sales.application.port.outbound.QuoteRepository;
import com.erp.sales.domain.entity.Quote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteExpirationSchedulerTest {

    @Mock QuoteRepository quoteRepository;
    @Mock QuoteUseCase quoteUseCase;
    @InjectMocks QuoteExpirationScheduler scheduler;

    @Test
    void 만료일이_지난_견적만_expireQuote_호출() {
        Quote expired = Quote.issue(1L, Money.of(1000), LocalDate.now().minusDays(1));
        expired.assignId(1L);
        Quote alive = Quote.issue(2L, Money.of(1000), LocalDate.now().plusDays(10));
        alive.assignId(2L);

        when(quoteRepository.findActiveExpirableQuotes()).thenReturn(List.of(expired, alive));

        scheduler.expireOutdatedQuotes();

        verify(quoteUseCase).expireQuote(1L);
        verify(quoteUseCase, never()).expireQuote(2L);
    }

    @Test
    void 만료_가능한_견적이_없으면_호출_없음() {
        when(quoteRepository.findActiveExpirableQuotes()).thenReturn(List.of());

        scheduler.expireOutdatedQuotes();

        verify(quoteUseCase, never()).expireQuote(anyLong());
    }
}