package com.erp.auth.application.port.inbound;

import com.erp.auth.application.dto.command.AssignRoleCommand;
import com.erp.auth.application.dto.command.LoginCommand;

public interface AuthUseCase {
    boolean login(LoginCommand command);
}