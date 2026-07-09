package com.ncsmap.community.post.dto;

import com.ncsmap.community.post.entity.PostCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private Long id;

    private Long memberId;
    private String memberNickname;

    private PostCategory category;
    private String title;

    private int viewCount;
    private int likeCount;

    private LocalDateTime createdAt;
}
