package com.ncsmap.community.post.dto;

import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @NotNull(message = "카테고리는 필수입니다.")
    private PostCategory category;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .id(post.getId())
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .category(post.getCategory())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
