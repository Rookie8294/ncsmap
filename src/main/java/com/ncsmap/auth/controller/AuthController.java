package com.ncsmap.auth.controller;

import com.ncsmap.auth.dto.LoginRequest;
import com.ncsmap.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "로그인 기능 구현")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<Long> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session
    ) {
        Long memberId = authService.login(request, session);

        return ResponseEntity.ok(memberId);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        authService.logout(session);

        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @Operation(summary = "로그인 상태 확인")
    @PostMapping("/me")
    public ResponseEntity<Long> me(HttpSession session) {
        Long memberId = (Long)session.getAttribute("LOGIN_MEMBER_ID");

        if(memberId == null){
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(memberId);
    }

}