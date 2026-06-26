package com.ncsmap.member.service;

import com.ncsmap.auth.security.CustomUserDetails;
import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.member.dto.MemberDetailResponse;
import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.entity.Provider;
import com.ncsmap.member.entity.Role;
import com.ncsmap.member.dto.SignUpRequest;
import com.ncsmap.member.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "회원가입")
    @Override
    public MemberResponse signUp(SignUpRequest request) {

        validateDuplicateEmail(request.getEmail());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        memberRepository.save(member);

        return new MemberResponse(member);
    }

    @Override
    public void validateDuplicateEmail(String email) {

        if(memberRepository.existsByEmail(email)){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    @Operation(summary = "회원 조회")
    @Override
    public MemberDetailResponse getMyInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {

            log.warn("로그인 사용자 조회 실패 reason=인증 정보 없음");
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Member member = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> {
                    log.warn("로그인 사용자 조회 실패 memberId={}", userDetails.getMemberId());
                    return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
                });

        log.info("로그인 사용자 조회 성공 memberId={}", member.getId());

        return new MemberDetailResponse(member);
    }

}
