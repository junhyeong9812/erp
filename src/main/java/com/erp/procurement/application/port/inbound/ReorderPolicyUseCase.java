package com.erp.procurement.application.port.inbound;

import com.erp.procurement.application.dto.command.RegisterReorderPolicyCommand;

public interface ReorderPolicyUseCase {
    Long registerReorderPolicy(RegisterReorderPolicyCommand command);
}
