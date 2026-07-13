package com.ncsmap.community.post.repository;

import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.BoardType;
import com.ncsmap.community.post.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // 삭제되지 않은 게시글 ID로 조회
    Optional<Post> findByIdAndStatus(Long memberId, PostStatus status);

    List<Post> findAllByStatusOrderByCreatedAtDesc(PostStatus status);
}
