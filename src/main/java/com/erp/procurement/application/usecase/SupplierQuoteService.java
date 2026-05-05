package com.erp.procurement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.support.IdGenerator;
import com.erp.procurement.application.dto.command.RegisterSupplierQuoteCommand;
import com.erp.procurement.application.port.inbound.SupplierQuoteUseCase;
import com.erp.procurement.application.port.outbound.SupplierQuoteRepository;
import com.erp.procurement.domain.entity.SupplierQuote;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupplierQuoteService implements SupplierQuoteUseCase {

    private final SupplierQuoteRepository repository;

    public SupplierQuoteService(SupplierQuoteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long registerSupplierQuote(RegisterSupplierQuoteCommand cmd) {
        SupplierQuote quote = SupplierQuote.of(
                cmd.supplierId(), cmd.productId(), cmd.quantity(), Money.of(cmd.unitPrice()));
        quote.assignId(IdGenerator.next());
        repository.save(quote);
        return quote.getId();
    }
}
