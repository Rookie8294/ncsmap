package com.ncsmap.community.post.service;

import com.ncsmap.community.post.dto.PostCreateRequest;
import com.ncsmap.community.post.dto.PostResponse;
import com.ncsmap.community.post.dto.PostUpdateRequest;
import com.ncsmap.community.post.repository.PostRepository;
import com.ncsmap.institution.repository.InstitutionRepository;
import com.ncsmap.jobposting.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class postServiceImpl implements PostService {

    private final InstitutionRepository institutionRepository;
    private final JobPostingRepository jobPostingRepository;
    private final PostRepository postRepository;

    // 게시글 생성
    @Override
    public PostResponse createPost(PostCreateRequest postCreateRequest) {
        return null;
    }

    @Override
    public List<PostResponse> getPosts() {
        return List.of();
    }

    @Override
    public PostResponse getPost(Long postId) {
        return null;
    }

    @Override
    public PostResponse updatePost(Long postId, PostUpdateRequest postUpdateRequest) {
        return null;
    }

    @Override
    public void deletePost(Long postId) {

    }
}
