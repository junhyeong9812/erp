package com.erp.auth;

import com.erp.auth.application.dto.command.LoginCommand;
import com.erp.auth.application.port.inbound.AuthUseCase;
import com.erp.auth.application.port.outbound.UserRepository;
import com.erp.auth.domain.entity.User;
import com.erp.auth.domain.event.UserLoggedInEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServiceIntegrationTest {

    @Autowired AuthUseCase authUseCase;
    @Autowired UserRepository userRepository;
    @Autowired LoginEventRecorder recorder;

    @Component
    static class LoginEventRecorder {
        final List<UserLoggedInEvent> events = new ArrayList<>();
        @EventListener
        public void on(UserLoggedInEvent e) { events.add(e); }
    }

    @BeforeEach
    void setUp() {
        recorder.events.clear();
        User u = User.register("alice", "HASH:secret", 1L);
        userRepository.save(u);
    }

    @Test
    void 로그인_성공_시_UserLoggedInEvent_가_리스너에_전달() {
        boolean ok = authUseCase.login(new LoginCommand("alice", "secret"));

        assertThat(ok).isTrue();
        assertThat(recorder.events).hasSize(1);
        assertThat(recorder.events.get(0).username()).isEqualTo("alice");
    }

    @Test
    void 비밀번호_불일치_시_이벤트_미발행() {
        boolean ok = authUseCase.login(new LoginCommand("alice", "wrong"));

        assertThat(ok).isFalse();
        assertThat(recorder.events).isEmpty();
    }
}