package com.ncsmap.common.util;

import com.ncsmap.auth.security.LoginUser;
import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.member.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {

    // 현재 로그인한 회원을 반환
    public static Long getCurrentMemberId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        validateAuthentication(authentication);

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof LoginUser loginUser)) {
            log.warn(
                    "현재 회원 ID 조회 실패 reason=지원하지 않는 Principal 타입, principalType={}",
                    principal == null ? "null" : principal.getClass().getName()
            );

            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        Long memberId = loginUser.getMemberId();

        if (memberId == null) {
            log.warn("현재 회원 ID 조회 실패 reason=LoginUser의 memberId가 null");
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        log.debug("현재 로그인 회원 ID 조회 성공 memberId={}", memberId);

        return memberId;
    }

    // 현재 인증 정보가 유효한지 검증
    private static void validateAuthentication(Authentication authentication) {
        if (authentication == null) {
            log.warn("현재 회원 ID 조회 실패 reason=Authentication 없음");
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        if (!authentication.isAuthenticated()) {
            log.warn("현재 회원 ID 조회 실패 reason=인증되지 않은 Authentication");
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        if (authentication instanceof AnonymousAuthenticationToken) {
            log.warn("현재 회원 ID 조회 실패 reason=익명 사용자");
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }

        if (authentication.getPrincipal() == null) {
            log.warn("현재 회원 ID 조회 실패 reason=Principal 없음");
            throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
        }
    }
}
