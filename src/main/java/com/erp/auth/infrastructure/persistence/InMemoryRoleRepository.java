package com.erp.auth.infrastructure.persistence;

import com.erp.auth.application.port.outbound.RoleRepository;
import com.erp.auth.application.port.outbound.UserRepository;
import com.erp.auth.domain.entity.Role;
import com.erp.auth.domain.entity.User;
import com.erp.common.persistence.InMemoryRepository;
import com.erp.common.support.IdGenerator;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryRoleRepository extends InMemoryRepository<Role, Long> implements RoleRepository {
    @Override protected Long extractId(Role r) { return r.getId(); }
    @Override public Role save(Role role) {
        if (role.getId() == null) role.assignId(IdGenerator.next());
        return super.save(role);
    }
    @Override public Optional<Role> findByCode(String code) {
        return findAllBy(r -> r.getCode().equals(code)).stream().findFirst();
    }
}