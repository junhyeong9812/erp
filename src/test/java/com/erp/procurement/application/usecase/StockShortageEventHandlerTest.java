package com.erp.procurement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.inventory.domain.event.StockDepletedEvent;
import com.erp.procurement.application.dto.command.IssuePurchaseOrderCommand;
import com.erp.procurement.application.port.inbound.PurchaseOrderUseCase;
import com.erp.procurement.application.port.outbound.ReorderPolicyRepository;
import com.erp.procurement.application.port.outbound.SupplierQuoteRepository;
import com.erp.procurement.domain.entity.ReorderPolicy;
import com.erp.procurement.domain.entity.SupplierQuote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockShortageEventHandlerTest {

    @Mock ReorderPolicyRepository reorderPolicyRepository;
    @Mock SupplierQuoteRepository supplierQuoteRepository;
    @Mock PurchaseOrderUseCase purchaseOrderUseCase;
    @InjectMocks StockShortageEventHandler handler;

    @Test
    void 정책과_견적이_있으면_정책값으로_자동_발주() {
        StockDepletedEvent event = new StockDepletedEvent(1L, 100L, Instant.now());
        ReorderPolicy policy = ReorderPolicy.of(100L, 7L, 200);
        SupplierQuote quote = SupplierQuote.of(7L, 100L, 50, Money.of(1234));

        when(reorderPolicyRepository.findByProductId(100L)).thenReturn(Optional.of(policy));
        when(supplierQuoteRepository.findLatestByProductAndSupplier(100L, 7L))
                .thenReturn(Optional.of(quote));

        handler.on(event);

        ArgumentCaptor<IssuePurchaseOrderCommand> cap =
                ArgumentCaptor.forClass(IssuePurchaseOrderCommand.class);
        verify(purchaseOrderUseCase).issuePurchaseOrder(cap.capture());
        IssuePurchaseOrderCommand cmd = cap.getValue();
        assertThat(cmd.supplierId()).isEqualTo(7L);
        assertThat(cmd.productId()).isEqualTo(100L);
        assertThat(cmd.quantity()).isEqualTo(200);
        assertThat(cmd.unitPrice()).isEqualTo(1234L);
    }

    @Test
    void 정책_없으면_발주_스킵() {
        StockDepletedEvent event = new StockDepletedEvent(1L, 100L, Instant.now());
        when(reorderPolicyRepository.findByProductId(100L)).thenReturn(Optional.empty());

        handler.on(event);

        verify(purchaseOrderUseCase, never()).issuePurchaseOrder(any());
        verifyNoInteractions(supplierQuoteRepository);
    }

    @Test
    void 견적_없으면_발주_스킵() {
        StockDepletedEvent event = new StockDepletedEvent(1L, 100L, Instant.now());
        ReorderPolicy policy = ReorderPolicy.of(100L, 7L, 200);
        when(reorderPolicyRepository.findByProductId(100L)).thenReturn(Optional.of(policy));
        when(supplierQuoteRepository.findLatestByProductAndSupplier(100L, 7L))
                .thenReturn(Optional.empty());

        handler.on(event);

        verify(purchaseOrderUseCase, never()).issuePurchaseOrder(any());
    }
}