package com.ncsmap.member.service;

import com.ncsmap.member.dto.MemberDetailResponse;
import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.dto.MemberUpdateRequest;
import com.ncsmap.member.dto.SignUpRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    // 화원 가입
    MemberResponse signUp(SignUpRequest request);

    // 이메일 중복 검증
    void validateDuplicateEmail(String email);

    // 회원 조회
    MemberDetailResponse getMyInfo();

    // 회원 정보 수정
    MemberDetailResponse updateMyInfo(MemberUpdateRequest request, MultipartFile file);

    // 회원 탈퇴
}
