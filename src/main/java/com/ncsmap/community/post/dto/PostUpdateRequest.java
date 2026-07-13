package com.ncsmap.community.post.dto;

import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @NotNull(message = "게시판 유형은 필수입니다.")
    private BoardType boardType;

    private Long jobPostingId;

    private Long institutionId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .postId(post.getId())
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .boardType(post.getBoardType())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
