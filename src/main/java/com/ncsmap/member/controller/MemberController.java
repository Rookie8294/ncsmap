package com.ncsmap.member.controller;

import com.ncsmap.auth.security.LoginUser;
import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.common.response.ApiResponse;
import com.ncsmap.member.dto.MemberDetailResponse;
import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.dto.MemberUpdateRequest;
import com.ncsmap.member.dto.SignUpRequest;
import com.ncsmap.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {

        MemberResponse response = memberService.signUp(request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "회원가입 성공",
                        response
                )
        );
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMyInfo(
            @AuthenticationPrincipal LoginUser loginUser) {
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        MemberDetailResponse response = memberService.getMyInfo(loginUser.getMemberId());

        return ResponseEntity.ok(
                ApiResponse.success("회원 정보 조회 성공", response)
        );
    }

    @Operation(summary = "회원 정보 수정")
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<MemberDetailResponse>> updateMyInfo(
            @ModelAttribute MemberUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file){
        MemberDetailResponse response = memberService.updateMyInfo(request, file);

        return ResponseEntity.ok(
                ApiResponse.success("회원정보 수정 성공", response)
        );
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @AuthenticationPrincipal LoginUser loginUser,
            HttpServletRequest request
    ) {
        memberService.withdraw(loginUser.getMemberId());

        request.getSession().invalidate();

        return ResponseEntity.ok(
                ApiResponse.success("회원탈퇴 성공", null)
        );
    }
}