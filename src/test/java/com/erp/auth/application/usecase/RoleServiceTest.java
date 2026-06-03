package com.erp.auth.application.usecase;

import com.erp.auth.application.dto.command.AssignRoleCommand;
import com.erp.auth.application.port.outbound.UserRepository;
import com.erp.auth.domain.entity.User;
import com.erp.common.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    private UserRepository userRepository;
    private RoleService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new RoleService(userRepository);
    }

    @Test
    void 존재하지_않는_사용자에_역할_부여하면_NotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.assignRoleToUser(new AssignRoleCommand(999L, "ROLE_X")))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void 역할_부여_성공_시_save_호출되고_사용자_역할_집합에_포함() {
        User user = User.register("alice", "HASH:x", 1L);
        user.assignId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.assignRoleToUser(new AssignRoleCommand(1L, "ROLE_SALES"));

        assertThat(user.getRoleCodes()).contains("ROLE_SALES");
        verify(userRepository).save(user);
    }
}