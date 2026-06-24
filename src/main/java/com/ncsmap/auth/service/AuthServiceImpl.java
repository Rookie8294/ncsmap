package com.ncsmap.auth.service;

import com.ncsmap.auth.dto.LoginRequest;
import com.ncsmap.auth.security.CustomUserDetails;
import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.member.domain.Member;
import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final MemberRepository  memberRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    public Long login(LoginRequest request, HttpServletRequest httpRequest) {
        log.info("로그인 시도 email={}", request.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    securityContext
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            log.info("로그인 성공 memberId={}, email={}", userDetails.getMemberId(),  userDetails.getEmail());

            return userDetails.getMemberId();
        } catch (Exception e) {
            log.warn("로그인 실패 email={}, reason=잘못된 인증 정보", request.getEmail());
            throw new BusinessException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }
    }

    @Override
    public void logout(HttpSession session) {
        log.info("로그아웃 요청");

        SecurityContextHolder.clearContext();

        if(session != null) {
            session.invalidate();
            log.info("세션 무효화 완료");
        }
    }

    @Override
    public MemberResponse getLoginMember() {
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

        return new MemberResponse(member);

    }
}
