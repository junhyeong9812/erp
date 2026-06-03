package com.erp.auth.presentation.api;

import com.erp.auth.application.dto.command.AssignRoleCommand;
import com.erp.auth.application.port.inbound.RoleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean
    RoleUseCase roleUseCase;

    @Test
    void assign_호출_시_UseCase_에_Command_전달() throws Exception {
        mvc.perform(post("/api/auth/roles/assign")
                        .param("userId", "1")
                        .param("roleCode", "ROLE_SALES"))
                .andExpect(status().isOk());

        verify(roleUseCase).assignRoleToUser(argThat((AssignRoleCommand c) ->
                c.userId() == 1L && c.roleCode().equals("ROLE_SALES")));
    }
}