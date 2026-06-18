package com.ncsmap.member.service;

import com.ncsmap.member.dto.SignUpRequest;

public interface MemberService {

    Long signUp(SignUpRequest request);

    void validateDuplicateEmail(String email);
}
