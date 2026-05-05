package com.erp.sales.presentation.api;

import com.erp.sales.application.dto.command.CreateQuoteCommand;
import com.erp.sales.application.port.inbound.QuoteUseCase;
import com.erp.sales.presentation.dto.request.CreateQuoteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales/quotes")
public class QuoteController {

    private final QuoteUseCase quoteUseCase;

    public QuoteController(QuoteUseCase quoteUseCase) {
        this.quoteUseCase = quoteUseCase;
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CreateQuoteRequest req) {
        var lines = req.lines().stream()
                .map(l -> new CreateQuoteCommand.Line(l.productId(), l.quantity(), l.unitPrice()))
                .toList();
        Long id = quoteUseCase.createQuote(new CreateQuoteCommand(req.customerId(), lines, req.validUntil()));
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> accept(@PathVariable Long id) {
        quoteUseCase.acceptQuote(id);
        return ResponseEntity.noContent().build();
    }
}