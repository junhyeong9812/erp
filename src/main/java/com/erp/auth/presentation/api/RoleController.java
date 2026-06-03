package com.erp.auth.presentation.api;

import com.erp.auth.application.dto.command.AssignRoleCommand;
import com.erp.auth.application.port.inbound.RoleUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/roles")
public class RoleController {

    private final RoleUseCase roleUseCase;

    public RoleController(RoleUseCase roleUseCase) { this.roleUseCase = roleUseCase; }

    @PostMapping("/assign")
    public ResponseEntity<Void> assign(@RequestParam Long userId, @RequestParam String roleCode) {
        roleUseCase.assignRoleToUser(new AssignRoleCommand(userId, roleCode));
        return ResponseEntity.ok().build();
    }
}