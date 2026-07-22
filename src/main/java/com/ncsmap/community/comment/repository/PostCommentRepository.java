package com.ncsmap.community.comment.repository;

import com.ncsmap.community.comment.entity.PostComment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // 삭제되지 않은 댓글을 ID로 조회
    Optional<PostComment> findByIdAndDeletedFalse(Long id);

    // 특정 게시글의 댓글을 생성일 오름차순으로 조회
    @EntityGraph(attributePaths = {"member", "post", "parent"})
    List<PostComment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
}
