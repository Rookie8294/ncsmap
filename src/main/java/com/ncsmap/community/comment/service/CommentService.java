package com.ncsmap.community.comment.service;

import com.ncsmap.community.comment.dto.CommentCreateRequest;
import com.ncsmap.community.comment.dto.CommentResponse;
import com.ncsmap.community.comment.dto.CommentUpdateRequest;

import java.util.List;

public interface CommentService {

    // 게시글에 댓글 또는 대댓글 작성
    CommentResponse createComment(Long postId, CommentCreateRequest commentCreateRequest);

    List<CommentResponse> getComments(Long postId);

    CommentResponse updateComment(
            Long postId,
            Long commentId,
            CommentUpdateRequest commentUpdateRequest
    );

    void deleteComment(Long postId, Long commentId);
}
