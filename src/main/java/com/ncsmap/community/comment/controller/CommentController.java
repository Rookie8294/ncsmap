package com.ncsmap.community.comment.controller;

import com.ncsmap.common.response.ApiResponse;
import com.ncsmap.community.comment.dto.CommentCreateRequest;
import com.ncsmap.community.comment.dto.CommentResponse;
import com.ncsmap.community.comment.dto.CommentUpdateRequest;
import com.ncsmap.community.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment", description = "댓글 기능 구현")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        log.info(
                "댓글 작성 API 요청 postId={}, parentCommentId={}",
                postId,
                request.getParentCommentId()
        );

        CommentResponse response = commentService.createComment(postId, request);

        log.info(
                "댓글 작성 API 처리 완료 postId={}, commentId={}, parentCommentId={}",
                postId,
                response.getCommentId(),
                request.getParentCommentId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글 작성 성공", response));
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long postId
    ) {
        log.info("댓글 목록 조회 API 요청 postId={}", postId);

        List<CommentResponse> responses = commentService.getComments(postId);

        log.info(
                "댓글 목록 조회 API 처리 완료 postId={}, commentCount={}",
                postId,
                responses.size()
        );

        return ResponseEntity.ok(
                ApiResponse.success("댓글 목록 조회 성공", responses)
        );
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        log.info(
                "댓글 수정 API 요청 postId={}, commentId={}",
                postId,
                commentId
        );

        CommentResponse response = commentService.updateComment(
                postId,
                commentId,
                request
        );

        log.info(
                "댓글 수정 API 처리 완료 postId={}, commentId={}",
                postId,
                commentId
        );

        return ResponseEntity.ok(
                ApiResponse.success("댓글 수정 성공", response)
        );
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        log.info(
                "댓글 삭제 API 요청 postId={}, commentId={}",
                postId,
                commentId
        );

        commentService.deleteComment(postId, commentId);

        log.info(
                "댓글 삭제 API 처리 완료 postId={}, commentId={}",
                postId,
                commentId
        );

        return ResponseEntity.ok(
                ApiResponse.success("댓글 삭제 성공", null)
        );
    }
}
