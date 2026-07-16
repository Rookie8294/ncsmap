package com.ncsmap.community.comment.service;

import com.ncsmap.community.comment.dto.CommentCreateRequest;
import com.ncsmap.community.comment.dto.CommentResponse;

public interface CommentService {

    // 게시글에 댓글 또는 대댓글 작성
    CommentResponse createComment(Long postId, CommentCreateRequest commentCreateRequest);
}
