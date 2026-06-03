package com.erp.auth.application.port.outbound;

import com.erp.auth.domain.entity.Role;
import com.erp.auth.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
}