package com.erp.production.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.production.application.port.outbound.BomRepository;
import com.erp.production.domain.entity.BillOfMaterials;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryBomRepository extends InMemoryRepository<BillOfMaterials, Long> implements BomRepository {
    @Override protected Long extractId(BillOfMaterials b) { return b.getId(); }
    @Override public Optional<BillOfMaterials> findByFinishedProductId(Long productId) {
        return findAllBy(b -> b.getFinishedProductId().equals(productId)).stream().findFirst();
    }
}