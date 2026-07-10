package com.ncsmap.community.post.service;

import com.ncsmap.community.post.dto.PostCreateRequest;
import com.ncsmap.community.post.dto.PostResponse;
import com.ncsmap.community.post.dto.PostUpdateRequest;
import com.ncsmap.community.post.entity.Post;

import java.util.List;

public interface PostService {

    // 게시글 생성
    PostResponse createPost(PostCreateRequest postCreateRequest);

    // 게시글 목록 조회
    List<PostResponse> getPostList();

    // 게시글 상세 조회
    PostResponse getPost(Long postId);

    // 게시글 수정
    PostResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest);

    // 게시글 삭제
    void deletePost(Long postId);
}
