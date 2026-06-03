package com.erp.auth.application.usecase;

import com.erp.auth.application.dto.command.AssignRoleCommand;
import com.erp.auth.application.port.inbound.RoleUseCase;
import com.erp.auth.application.port.outbound.UserRepository;
import com.erp.auth.domain.entity.User;
import com.erp.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService implements RoleUseCase {

    private final UserRepository userRepository;

    public RoleService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public void assignRoleToUser(AssignRoleCommand cmd) {
        User user = userRepository.findById(cmd.userId()).orElseThrow(NotFoundException::new);
        user.assignRole(cmd.roleCode());
        userRepository.save(user);
    }
}