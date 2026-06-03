package com.erp.auth.application.usecase;

import com.erp.auth.application.dto.command.LoginCommand;
import com.erp.auth.application.port.outbound.UserRepository;
import com.erp.auth.domain.entity.User;
import com.erp.auth.domain.event.UserLoggedInEvent;
import com.erp.common.domain.DomainEvent;
import com.erp.common.messaging.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private EventBus eventBus;
    private AuthService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        eventBus = mock(EventBus.class);
        service = new AuthService(userRepository, eventBus);
    }

    @Test
    void 존재하지_않는_사용자는_로그인_실패() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        boolean ok = service.login(new LoginCommand("ghost", "x"));

        assertThat(ok).isFalse();
        verify(userRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }

    @Test
    void 비밀번호_불일치면_로그인_실패_이벤트_미발행() {
        User user = User.register("alice", "HASH:secret", 1L);
        user.assignId(1L);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        boolean ok = service.login(new LoginCommand("alice", "wrong"));

        assertThat(ok).isFalse();
        verify(userRepository, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }

    @Test
    void 로그인_성공_시_save_와_이벤트_발행이_모두_호출() {
        User user = User.register("alice", "HASH:secret", 1L);
        user.assignId(1L);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        boolean ok = service.login(new LoginCommand("alice", "secret"));

        assertThat(ok).isTrue();
        verify(userRepository).save(user);
        verify(eventBus).publishAll(argThat((List<DomainEvent> evts) ->
                evts.stream().anyMatch(e -> e instanceof UserLoggedInEvent)));
    }

    @Test
    void 로그인_성공_후_도메인_events_는_비워진다() {
        User user = User.register("alice", "HASH:secret", 1L);
        user.assignId(1L);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        service.login(new LoginCommand("alice", "secret"));

        // pullEvents 가 호출됐으므로 엔티티의 events 는 비어 있어야 함
        assertThat(user.events()).isEmpty();
    }
}