package com.ncsmap.auth.service;

import com.ncsmap.auth.dto.LoginRequest;
import com.ncsmap.member.domain.Member;
import com.ncsmap.member.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final MemberRepository  memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Long login(LoginRequest loginRequest, HttpSession session) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if(!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        session.setAttribute("LOGIN_MEMBER_ID", member.getId());

        return member.getId();
    }

    @Override
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
