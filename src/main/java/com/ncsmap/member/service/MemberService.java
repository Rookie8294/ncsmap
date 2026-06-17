package com.ncsmap.member.service;

import com.ncsmap.member.dto.SignUpRequest;

public interface MemberService {

    Long SignUp(SignUpRequest request);

    void validateDuplicateEmail(String email);
}
