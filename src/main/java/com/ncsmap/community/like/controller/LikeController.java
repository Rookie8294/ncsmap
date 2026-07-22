package com.ncsmap.community.like.controller;

import com.ncsmap.common.response.ApiResponse;
import com.ncsmap.community.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/likes")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "게시글 좋아요")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> likePost(@PathVariable Long postId) {
        likeService.likePost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 좋아요 성공", null));
    }

    @Operation(summary = "게시글 좋아요 취소")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> unlikePost(@PathVariable Long postId) {
        likeService.unlikePost(postId);
        return ResponseEntity.ok(ApiResponse.success("게시글 좋아요 취소 성공", null));
    }
}
