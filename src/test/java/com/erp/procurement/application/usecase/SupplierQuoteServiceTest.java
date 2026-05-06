package com.erp.procurement.application.usecase;

import com.erp.procurement.application.dto.command.RegisterSupplierQuoteCommand;
import com.erp.procurement.application.port.outbound.SupplierQuoteRepository;
import com.erp.procurement.domain.entity.SupplierQuote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SupplierQuoteServiceTest {

    @Mock SupplierQuoteRepository repository;
    @InjectMocks SupplierQuoteService service;

    @Test
    void 견적_등록_시_id_부여_및_저장() {
        Long id = service.registerSupplierQuote(
                new RegisterSupplierQuoteCommand(7L, 100L, 50, 1234L));

        assertThat(id).isNotNull();
        ArgumentCaptor<SupplierQuote> cap = ArgumentCaptor.forClass(SupplierQuote.class);
        verify(repository).save(cap.capture());
        SupplierQuote saved = cap.getValue();
        assertThat(saved.getSupplierId()).isEqualTo(7L);
        assertThat(saved.getProductId()).isEqualTo(100L);
        assertThat(saved.getUnitPrice()).isEqualTo(1234L);
        assertThat(saved.getId()).isEqualTo(id);
    }

    @Test
    void 같은_supplier_product_에_여러_견적_등록_허용() {
        Long first = service.registerSupplierQuote(
                new RegisterSupplierQuoteCommand(7L, 100L, 50, 1000L));
        Long second = service.registerSupplierQuote(
                new RegisterSupplierQuoteCommand(7L, 100L, 50, 1234L));

        assertThat(first).isNotEqualTo(second);
    }
}