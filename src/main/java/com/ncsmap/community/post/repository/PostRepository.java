package com.ncsmap.community.post.repository;

import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // 삭제되지 않은 게시글 ID로 조회
    Optional<Post> findByIdAndStatus(Long memberId, PostStatus status);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Post p
            set p.viewCount = p.viewCount + 1
            where p.id = :postId and p.status = :status
            """)
    int incrementViewCount(
            @Param("postId") Long postId,
            @Param("status") PostStatus status
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Post p
            set p.likeCount = p.likeCount + 1
            where p.id = :postId and p.status = :status
            """)
    int incrementLikeCount(
            @Param("postId") Long postId,
            @Param("status") PostStatus status
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Post p
            set p.likeCount = p.likeCount - 1
            where p.id = :postId
              and p.status = :status
              and p.likeCount > 0
            """)
    int decrementLikeCount(
            @Param("postId") Long postId,
            @Param("status") PostStatus status
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Post p
            set p.commentCount = p.commentCount + 1
            where p.id = :postId and p.status = :status
            """)
    int incrementCommentCount(
            @Param("postId") Long postId,
            @Param("status") PostStatus status
    );

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Post p
            set p.commentCount = p.commentCount - 1
            where p.id = :postId
              and p.status = :status
              and p.commentCount > 0
            """)
    int decrementCommentCount(
            @Param("postId") Long postId,
            @Param("status") PostStatus status
    );
}
