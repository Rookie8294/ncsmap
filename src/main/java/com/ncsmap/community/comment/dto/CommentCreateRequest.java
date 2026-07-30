package com.ncsmap.community.comment.dto;

import com.ncsmap.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    private Long parentCommentId;

    @NotBlank(message = "댓글을 입력해주세요.")
    private String content;
}
