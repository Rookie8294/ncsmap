package com.ncsmap.community.comment.dto;

import com.ncsmap.community.comment.entity.PostComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long commentId;

    private Long postId;

    private Long memberId;

    private String nickname;

    private String profileImg;

    private Long parentCommentId;

    private String content;

    private boolean deleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 댓글 엔티티를 댓글 응답 DTO로 변환합니다.
     */
    public static CommentResponse from(PostComment comment) {

        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .memberId(comment.getMember().getId())
                .nickname(comment.getMember().getNickname())
                .profileImg(comment.getMember().getProfileImg())
                .parentCommentId(
                        comment.getParent() != null
                                ? comment.getParent().getId()
                                : null
                )
                .content(
                        comment.isDeleted()
                                ? "삭제된 댓글입니다."
                                : comment.getContent()
                )
                .deleted(comment.isDeleted())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
