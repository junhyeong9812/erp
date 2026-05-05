package com.erp.procurement.application.usecase;

import com.erp.inventory.domain.event.StockDepletedEvent;
import com.erp.procurement.application.dto.command.IssuePurchaseOrderCommand;
import com.erp.procurement.application.port.inbound.PurchaseOrderUseCase;
import com.erp.procurement.application.port.outbound.ReorderPolicyRepository;
import com.erp.procurement.application.port.outbound.SupplierQuoteRepository;
import com.erp.procurement.domain.entity.ReorderPolicy;
import com.erp.procurement.domain.entity.SupplierQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StockShortageEventHandler {

    private static final Logger log = LoggerFactory.getLogger(StockShortageEventHandler.class);

    private final ReorderPolicyRepository reorderPolicyRepository;
    private final SupplierQuoteRepository supplierQuoteRepository;
    private final PurchaseOrderUseCase purchaseOrderUseCase;

    public StockShortageEventHandler(
            ReorderPolicyRepository reorderPolicyRepository,
            SupplierQuoteRepository supplierQuoteRepository,
            PurchaseOrderUseCase purchaseOrderUseCase) {
        this.reorderPolicyRepository = reorderPolicyRepository;
        this.supplierQuoteRepository = supplierQuoteRepository;
        this.purchaseOrderUseCase = purchaseOrderUseCase;
    }

    @ApplicationModuleListener
    public void on(StockDepletedEvent event) {
        Long productId = event.productId();

        Optional<ReorderPolicy> policyOpt = reorderPolicyRepository.findByProductId(productId);
        if (policyOpt.isEmpty()) {
            log.warn("재주문 정책 없음 — 자동 발주 스킵. productId={}", productId);
            return;
        }
        ReorderPolicy policy = policyOpt.get();

        Optional<SupplierQuote> quoteOpt = supplierQuoteRepository
                .findLatestByProductAndSupplier(productId, policy.getDefaultSupplierId());
        if (quoteOpt.isEmpty()) {
            log.warn("최신 견적 없음 — 자동 발주 스킵. productId={}, supplierId={}",
                    productId, policy.getDefaultSupplierId());
            return;
        }
        SupplierQuote quote = quoteOpt.get();

        purchaseOrderUseCase.issuePurchaseOrder(new IssuePurchaseOrderCommand(
                policy.getDefaultSupplierId(),
                productId,
                policy.getReorderQuantity(),
                quote.getUnitPrice()));
    }
}
