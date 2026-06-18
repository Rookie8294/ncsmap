package com.ncsmap.auth.service;

import com.ncsmap.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;

public interface AuthService {

    Long login(LoginRequest loginRequest, HttpSession session);

    void logout(HttpSession session);
}
