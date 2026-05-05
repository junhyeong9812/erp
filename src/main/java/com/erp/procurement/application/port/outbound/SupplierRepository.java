package com.erp.procurement.application.port.outbound;

import com.erp.procurement.domain.entity.PurchaseOrder;
import com.erp.procurement.domain.entity.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository {
    Supplier save(Supplier supplier);
    Optional<Supplier> findById(Long id);
}