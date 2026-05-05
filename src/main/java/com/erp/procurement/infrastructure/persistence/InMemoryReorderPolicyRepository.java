package com.erp.procurement.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.procurement.application.port.outbound.ReorderPolicyRepository;
import com.erp.procurement.domain.entity.ReorderPolicy;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryReorderPolicyRepository extends InMemoryRepository<ReorderPolicy, Long>
        implements ReorderPolicyRepository {
    @Override protected Long extractId(ReorderPolicy p) { return p.getId(); }

    @Override
    public Optional<ReorderPolicy> findByProductId(Long productId) {
        return findAllBy(p -> p.getProductId().equals(productId)).stream().findFirst();
    }
}
