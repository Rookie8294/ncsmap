package com.ncsmap.auth.security;

import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository  memberRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("회원 인증 정보 조회 email = {}", email);

        Member member = memberRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> {
                    log.warn("화원 인증 정보 조회 실패 email = {}", email);
                    return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
                });

        log.info("화원 인증 정보 조회 성공 memberId = {}", member.getId());

        return LoginUser.from(
                member,
                List.of(new SimpleGrantedAuthority(member.getRole().name()))
        );
    }
}
