package com.erp.procurement.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.procurement.application.port.outbound.SupplierRepository;
import com.erp.procurement.domain.entity.Supplier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemorySupplierRepository extends InMemoryRepository<Supplier, Long> implements SupplierRepository {
    @Override protected Long extractId(Supplier s) { return s.getId(); }
}