package com.erp.procurement.presentation.api;

import com.erp.procurement.application.dto.command.RegisterReorderPolicyCommand;
import com.erp.procurement.application.port.inbound.ReorderPolicyUseCase;
import com.erp.procurement.presentation.dto.request.RegisterReorderPolicyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procurement/reorder-policies")
public class ReorderPolicyController {

    private final ReorderPolicyUseCase useCase;

    public ReorderPolicyController(ReorderPolicyUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<Long> register(@RequestBody RegisterReorderPolicyRequest req) {
        Long id = useCase.registerReorderPolicy(new RegisterReorderPolicyCommand(
                req.productId(), req.defaultSupplierId(), req.reorderQuantity()));
        return ResponseEntity.ok(id);
    }
}
