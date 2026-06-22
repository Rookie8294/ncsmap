package com.ncsmap.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final Long memberId;

    public LoginResponse(Long memberId) {
        this.memberId = memberId;
    }
}
