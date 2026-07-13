package com.ncsmap.community.post.repository;

import com.ncsmap.community.post.dto.PostSearchCondition;
import com.ncsmap.community.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    // 검색 조건에 맞는 게시글 목록 페이징 조회
    Page<Post> searchPost(PostSearchCondition postSearchCondition, Pageable pageable);
}
