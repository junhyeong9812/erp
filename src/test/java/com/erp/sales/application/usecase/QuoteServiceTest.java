package com.erp.sales.application.usecase;

import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.sales.application.dto.command.CreateQuoteCommand;
import com.erp.sales.application.port.outbound.QuoteRepository;
import com.erp.sales.domain.entity.Quote;
import com.erp.sales.domain.event.QuoteExpiredEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock QuoteRepository quoteRepository;
    @Mock EventBus eventBus;
    @InjectMocks QuoteService service;

    @Test
    void createQuote_는_총액을_unitPrice_곱_quantity_합산하여_저장() {
        when(quoteRepository.save(any(Quote.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateQuoteCommand cmd = new CreateQuoteCommand(
                1L,
                List.of(
                        new CreateQuoteCommand.Line(100L, 2, 1000L),
                        new CreateQuoteCommand.Line(200L, 3, 500L)
                ),
                LocalDate.of(2030, 1, 1));

        Long id = service.createQuote(cmd);

        ArgumentCaptor<Quote> captor = ArgumentCaptor.forClass(Quote.class);
        verify(quoteRepository).save(captor.capture());
        Quote saved = captor.getValue();

        assertThat(id).isNotNull();
        assertThat(saved.getCustomerId()).isEqualTo(1L);
        assertThat(saved.getTotalAmount().amount().longValueExact()).isEqualTo(3500L);
        assertThat(saved.getValidUntil()).isEqualTo(LocalDate.of(2030, 1, 1));
    }

    @Test
    void expireQuote_존재하지_않으면_NotFoundException() {
        when(quoteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.expireQuote(999L))
                .isInstanceOf(NotFoundException.class);

        verify(eventBus, never()).publishAll(anyList());
    }

    @Test
    void expireQuote_는_도메인_이벤트를_EventBus_로_발행() {
        Quote q = Quote.issue(1L, com.erp.common.domain.Money.of(5000), LocalDate.of(2026, 1, 1));
        q.assignId(42L);
        when(quoteRepository.findById(42L)).thenReturn(Optional.of(q));
        when(quoteRepository.save(any(Quote.class))).thenAnswer(inv -> inv.getArgument(0));

        service.expireQuote(42L);

        verify(quoteRepository).save(q);
        ArgumentCaptor<List<? extends com.erp.common.domain.DomainEvent>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(eventBus).publishAll(captor.capture());
        assertThat(captor.getValue()).hasAtLeastOneElementOfType(QuoteExpiredEvent.class);
        assertThat(q.getStatus()).isEqualTo(Quote.Status.EXPIRED);
    }

    @Test
    void acceptQuote_는_상태를_ACCEPTED_로_전이() {
        Quote q = Quote.issue(1L, com.erp.common.domain.Money.of(5000), LocalDate.of(2030, 1, 1));
        q.assignId(7L);
        when(quoteRepository.findById(7L)).thenReturn(Optional.of(q));
        when(quoteRepository.save(any(Quote.class))).thenAnswer(inv -> inv.getArgument(0));

        service.acceptQuote(7L);

        assertThat(q.getStatus()).isEqualTo(Quote.Status.ACCEPTED);
        verify(quoteRepository).save(q);
    }
}
