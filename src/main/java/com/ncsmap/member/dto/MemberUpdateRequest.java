package com.ncsmap.member.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateRequest {

    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    private String name;

    @Size(max = 50, message = "닉네임은 50자 이하로 입력해주세요.")
    private String nickname;

    @Size(max = 500)
    private String profileImg;
}
