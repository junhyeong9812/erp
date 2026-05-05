package com.erp.procurement.application.usecase;

import com.erp.common.support.IdGenerator;
import com.erp.procurement.application.dto.command.RegisterReorderPolicyCommand;
import com.erp.procurement.application.port.inbound.ReorderPolicyUseCase;
import com.erp.procurement.application.port.outbound.ReorderPolicyRepository;
import com.erp.procurement.domain.entity.ReorderPolicy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReorderPolicyService implements ReorderPolicyUseCase {

    private final ReorderPolicyRepository repository;

    public ReorderPolicyService(ReorderPolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Long registerReorderPolicy(RegisterReorderPolicyCommand cmd) {
        repository.findByProductId(cmd.productId()).ifPresent(existing -> {
            throw new IllegalStateException(
                    "이미 재주문 정책이 존재합니다. productId=" + cmd.productId());
        });
        ReorderPolicy policy = ReorderPolicy.of(
                cmd.productId(), cmd.defaultSupplierId(), cmd.reorderQuantity());
        policy.assignId(IdGenerator.next());
        repository.save(policy);
        return policy.getId();
    }
}
