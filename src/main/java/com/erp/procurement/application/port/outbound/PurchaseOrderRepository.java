package com.erp.procurement.application.port.outbound;

import com.erp.procurement.domain.entity.PurchaseOrder;
import com.erp.procurement.domain.entity.Supplier;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository {
    PurchaseOrder save(PurchaseOrder order);
    Optional<PurchaseOrder> findById(Long id);
    List<PurchaseOrder> findByProduct(Long productId);
}