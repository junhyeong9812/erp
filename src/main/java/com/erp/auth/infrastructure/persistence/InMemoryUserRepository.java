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
public class InMemoryUserRepository extends InMemoryRepository<User, Long> implements UserRepository {
    @Override protected Long extractId(User u) { return u.getId(); }
    @Override public User save(User user) {
        if (user.getId() == null) user.assignId(IdGenerator.next());
        return super.save(user);
    }
    @Override public Optional<User> findByUsername(String username) {
        return findAllBy(u -> u.getUsername().equals(username)).stream().findFirst();
    }
}