package com.ncsmap.member.dto;

import com.ncsmap.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberDetailResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final String role;
    private final String provider;
    private final String profileImg;

    public MemberDetailResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.nickname = member.getNickname();
        this.role = member.getRole().name();
        this.provider = member.getProvider().name();
        this.profileImg = member.getProfileImg();
    }
}
