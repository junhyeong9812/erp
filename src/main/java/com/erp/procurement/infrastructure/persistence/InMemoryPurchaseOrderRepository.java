package com.erp.procurement.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.procurement.application.port.outbound.PurchaseOrderRepository;
import com.erp.procurement.domain.entity.PurchaseOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryPurchaseOrderRepository extends InMemoryRepository<PurchaseOrder, Long>
        implements PurchaseOrderRepository {
    @Override protected Long extractId(PurchaseOrder po) { return po.getId(); }
    @Override public List<PurchaseOrder> findByProduct(Long productId) {
        return findAllBy(po -> po.getProductId().equals(productId));
    }
}