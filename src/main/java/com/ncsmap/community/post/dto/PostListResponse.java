package com.ncsmap.community.post.dto;

import com.ncsmap.community.post.entity.BoardType;
import com.ncsmap.community.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private Long postId;

    private Long memberId;
    private String memberNickname;

    private BoardType boardType;
    private Long jobPostingId;
    private String jobPostingTitle;

    private Long institutionId;
    private String institutionName;

    private String title;

    private int viewCount;
    private int likeCount;

    private LocalDateTime createdAt;

    // 게시글 엔티티를 게시글 목록 응답으로 변환
    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .postId(post.getId())
                .memberId(post.getMember().getId())
                .memberNickname(post.getMember().getNickname())
                .boardType(post.getBoardType())
                .jobPostingId(
                        post.getJobPosting() != null
                                ? post.getJobPosting().getId()
                                : null
                )
                .jobPostingTitle(
                        post.getJobPosting() != null
                                ? post.getJobPosting().getTitle()
                                : null
                )
                .institutionId(
                        post.getInstitution() != null
                                ? post.getInstitution().getId()
                                : null
                )
                .institutionName(
                        post.getInstitution() != null
                                ? post.getInstitution().getName()
                                : null
                )
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
