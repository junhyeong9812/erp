package com.erp.procurement.application.port.inbound;

import com.erp.procurement.application.dto.command.RegisterSupplierQuoteCommand;

public interface SupplierQuoteUseCase {
    Long registerSupplierQuote(RegisterSupplierQuoteCommand command);
}
