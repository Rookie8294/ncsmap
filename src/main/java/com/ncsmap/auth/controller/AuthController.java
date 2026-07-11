package com.ncsmap.auth.controller;

import com.ncsmap.auth.dto.LoginRequest;
import com.ncsmap.auth.dto.LoginResponse;
import com.ncsmap.auth.service.AuthService;
import com.ncsmap.common.response.ApiResponse;
import com.ncsmap.member.dto.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        Long memberId = authService.login(request, httpRequest);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "로그인 성공",
                        new LoginResponse(memberId)));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        authService.logout(session);

        return ResponseEntity.ok(
                ApiResponse.success(
                "로그아웃 되었습니다.",
                null)
        );
    }

}