package com.ncsmap.community.post.dto;

import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.BoardType;
import com.ncsmap.community.post.entity.PostStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {

    private Long id;

    private Long memberId;
    private String memberNickname;

    private Long jobPostingId;
    private String jobPostingTitle;

    private Long institutionId;
    private String institutionName;

    private BoardType boardType;
    private String title;
    private String content;

    private int viewCount;
    private int likeCount;

    private PostStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .jobPostingId(post.getJobPosting() != null ? post.getJobPosting().getId() : null)
                .jobPostingTitle(post.getJobPosting() != null ? post.getJobPosting().getTitle() : null)
                .institutionId(post.getInstitution() != null ? post.getInstitution().getId() : null)
                .institutionName(post.getInstitution() != null ? post.getInstitution().getName() : null)
                .boardType(post.getBoardType())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .status(post.getStatus())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
