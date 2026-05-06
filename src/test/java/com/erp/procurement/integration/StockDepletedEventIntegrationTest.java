package com.erp.procurement.integration;

import com.erp.common.domain.Money;
import com.erp.common.messaging.SpringEventBus;
import com.erp.inventory.domain.event.StockDepletedEvent;
import com.erp.procurement.application.dto.command.IssuePurchaseOrderCommand;
import com.erp.procurement.application.port.inbound.PurchaseOrderUseCase;
import com.erp.procurement.application.port.outbound.ReorderPolicyRepository;
import com.erp.procurement.application.port.outbound.SupplierQuoteRepository;
import com.erp.procurement.application.usecase.StockShortageEventHandler;
import com.erp.procurement.domain.entity.ReorderPolicy;
import com.erp.procurement.domain.entity.SupplierQuote;
import com.erp.procurement.infrastructure.persistence.InMemoryReorderPolicyRepository;
import com.erp.procurement.infrastructure.persistence.InMemorySupplierQuoteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        SpringEventBus.class,
        StockDepletedEventIntegrationTest.TestConfig.class
})
class StockDepletedEventIntegrationTest {

    @Configuration
    static class TestConfig {
        @Bean PurchaseOrderUseCase purchaseOrderUseCase() { return mock(PurchaseOrderUseCase.class); }

        @Bean ReorderPolicyRepository reorderPolicyRepository() {
            InMemoryReorderPolicyRepository repo = new InMemoryReorderPolicyRepository();
            ReorderPolicy p = ReorderPolicy.of(100L, 7L, 200);
            p.assignId(1L);
            repo.save(p);
            return repo;
        }

        @Bean SupplierQuoteRepository supplierQuoteRepository() {
            InMemorySupplierQuoteRepository repo = new InMemorySupplierQuoteRepository();
            SupplierQuote q = SupplierQuote.of(7L, 100L, 50, Money.of(1234));
            q.assignId(1L);
            repo.save(q);
            return repo;
        }

        @Bean StockShortageEventHandler stockShortageEventHandler(
                ReorderPolicyRepository policyRepo,
                SupplierQuoteRepository quoteRepo,
                PurchaseOrderUseCase useCase) {
            return new StockShortageEventHandler(policyRepo, quoteRepo, useCase);
        }
    }

    @Autowired SpringEventBus bus;
    @Autowired PurchaseOrderUseCase purchaseOrderUseCase;

    @Test
    void StockDepletedEvent_발행_시_정책과_견적_기반_자동_발주() {
        bus.publish(new StockDepletedEvent(5L, 100L, Instant.now()));

        ArgumentCaptor<IssuePurchaseOrderCommand> cap =
                ArgumentCaptor.forClass(IssuePurchaseOrderCommand.class);
        verify(purchaseOrderUseCase).issuePurchaseOrder(cap.capture());

        IssuePurchaseOrderCommand cmd = cap.getValue();
        assertThat(cmd.supplierId()).isEqualTo(7L);
        assertThat(cmd.productId()).isEqualTo(100L);
        assertThat(cmd.quantity()).isEqualTo(200);
        assertThat(cmd.unitPrice()).isEqualTo(1234L);
    }
}