package com.ncsmap.community.post.repository;

import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.PostCategory;
import com.ncsmap.community.post.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 삭제되지 않은 게시글 ID로 조회
    Optional<Post> findByIdAndStatus(Long memberId, PostStatus status);

    // 삭제되지 않은 게시글 최신순으로 조회
    List<Post> findAllByStatusOrderByCreatedAtDesc(PostStatus status);

    // 카테고리별 게시글 목록 최신순 조회
    List<Post> findAllByCategoryAndStatusOrderByCreatedAtDesc(PostCategory category, PostStatus status);

    // 특정 회원이 작성한 게시글 목록 최신순 조회
    List<Post> findAllByMemberIdAndStatusOrderByCreatedAtDesc(Long memberId, PostStatus status);
}
