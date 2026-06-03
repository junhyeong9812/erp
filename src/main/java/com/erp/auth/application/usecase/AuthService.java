package com.erp.auth.application.usecase;

import com.erp.auth.application.dto.command.LoginCommand;
import com.erp.auth.application.port.inbound.AuthUseCase;
import com.erp.auth.application.port.outbound.UserRepository;
import com.erp.auth.domain.entity.User;
import com.erp.common.messaging.EventBus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final EventBus eventBus;
    // 학습용: 그냥 문자열 비교 해시. 실무는 BCrypt
    private final User.PasswordHasher hasher = (raw, hashed) -> hashed.equals("HASH:" + raw);

    public AuthService(UserRepository userRepository, EventBus eventBus) {
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    @Override
    public boolean login(LoginCommand cmd) {
        User user = userRepository.findByUsername(cmd.username()).orElse(null);
        if (user == null) return false;
        boolean ok = user.login(cmd.password(), hasher);
        if (ok) {
            userRepository.save(user);
            eventBus.publishAll(user.pullEvents());
        }
        return ok;
    }
}