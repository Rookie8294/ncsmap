package com.ncsmap.auth.controller;

import com.ncsmap.auth.dto.LoginRequest;
import com.ncsmap.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Long> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ) {
        Long memberId = authService.login(request, session);

        return ResponseEntity.ok(memberId);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        authService.logout(session);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }
}