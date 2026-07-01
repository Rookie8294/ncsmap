package com.ncsmap.member.controller;

import com.ncsmap.common.response.ApiResponse;
import com.ncsmap.member.dto.MemberDetailResponse;
import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.dto.MemberUpdateRequest;
import com.ncsmap.member.dto.SignUpRequest;
import com.ncsmap.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
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
    public ResponseEntity<ApiResponse<MemberDetailResponse>> getMyInfo() {
        MemberDetailResponse response = memberService.getMyInfo();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "회원 조회 성공",
                        response
                )
        );
    }

    @Operation(summary = "회원 정보 수정")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberDetailResponse>> updateMyInfo(
            @Valid @RequestBody MemberUpdateRequest request){
        MemberDetailResponse response = memberService.updateMyInfo(request);

        return ResponseEntity.ok(
                ApiResponse.success("회원정보 수정 성공", response)
        );
    }
}