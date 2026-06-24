package com.ncsmap.member.service;

import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.entity.Provider;
import com.ncsmap.member.entity.Role;
import com.ncsmap.member.dto.SignUpRequest;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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

    @Override
    public MemberResponse getMyInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


    }
}
