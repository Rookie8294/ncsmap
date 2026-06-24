package com.ncsmap.member.service;

import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.dto.SignUpRequest;

public interface MemberService {

    MemberResponse signUp(SignUpRequest request);

    void validateDuplicateEmail(String email);

    MemberResponse getMyInfo();
}
