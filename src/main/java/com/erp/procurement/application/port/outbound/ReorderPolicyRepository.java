package com.erp.procurement.application.port.outbound;

import com.erp.procurement.domain.entity.ReorderPolicy;

import java.util.Optional;

public interface ReorderPolicyRepository {
    ReorderPolicy save(ReorderPolicy policy);
    Optional<ReorderPolicy> findByProductId(Long productId);
}
