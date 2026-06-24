package com.ncsmap.auth.service;

import com.ncsmap.auth.dto.LoginRequest;
import com.ncsmap.member.dto.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public interface AuthService {

    Long login(LoginRequest request, HttpServletRequest httpRequest);

    void logout(HttpSession session);

    MemberResponse getLoginMember();
}
