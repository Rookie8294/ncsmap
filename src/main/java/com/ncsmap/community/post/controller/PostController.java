package com.ncsmap.community.post.controller;

import com.ncsmap.common.response.ApiResponse;
import com.ncsmap.community.post.dto.*;
import com.ncsmap.community.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 게시글 작성 컨트롤러\
    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @Valid @RequestBody PostCreateRequest postCreateRequest
    ) {
        log.info("게시글 작성 API 요청 boardType={}, title={}", postCreateRequest.getBoardType(), postCreateRequest.getTitle());

        PostResponse postResponse = postService.createPost(postCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글 작성 성공", postResponse));
    }

    // 게시글 목록 조회
    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getPostList(
            @ModelAttribute PostSearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info(
                "게시글 목록 조회 API 요청 boardType={}, jobPostingId={}, institutionId={}, keyword={}, page={}, size={}",
                condition.getBoardType(),
                condition.getJobPostingId(),
                condition.getInstitutionId(),
                condition.getKeyword(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<PostListResponse> postList =
                postService.getPostList(condition, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("게시글 목록 조회 성공", postList)
        );
    }

    // 게시글 상세 조회
    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @PathVariable Long postId
    ){
        log.info("게시글 상세 조회 API 요청 postId={}", postId);

        PostResponse postResponse = postService.getPost(postId);

        return ResponseEntity.ok(
                ApiResponse.success("게시물 상세 조회 성공", postResponse)
        );
    }

    // 게시글 수정
    @Operation(summary = "게시글 수정")
    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest postUpdateRequest
    ){
        log.info("게시글 수정 API 요청 postId={}", postId);

        postService.updatePost(postId, postUpdateRequest);

        return ResponseEntity.ok(
                ApiResponse.success("게시글 수정 성공", null)
        );
    }

    // 게시글 삭제
    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId
    ) {
        log.info("게시글 삭제 API 요청 postId={}", postId);

        postService.deletePost(postId);

        return ResponseEntity.ok(
                ApiResponse.success("게시글 삭제 성공", null)
        );
    }

}
