package com.erp.production.application.port.outbound;

import com.erp.production.domain.entity.BillOfMaterials;
import com.erp.production.domain.entity.WorkOrder;

import java.util.Optional;

public interface BomRepository {
    BillOfMaterials save(BillOfMaterials bom);
    Optional<BillOfMaterials> findByFinishedProductId(Long productId);
}