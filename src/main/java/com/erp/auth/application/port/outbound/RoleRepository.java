package com.erp.auth.application.port.outbound;

import com.erp.auth.domain.entity.Role;
import com.erp.auth.domain.entity.User;

import java.util.Optional;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findByCode(String code);
}