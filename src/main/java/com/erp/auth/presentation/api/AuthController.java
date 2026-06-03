package com.erp.auth.presentation.api;

import com.erp.auth.application.dto.command.LoginCommand;
import com.erp.auth.application.port.inbound.AuthUseCase;
import com.erp.auth.presentation.dto.request.LoginRequest;
import com.erp.auth.presentation.dto.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) { this.authUseCase = authUseCase; }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        boolean ok = authUseCase.login(new LoginCommand(req.username(), req.password()));
        return ResponseEntity.ok(new LoginResponse(ok, ok ? "로그인 성공" : "실패"));
    }
}