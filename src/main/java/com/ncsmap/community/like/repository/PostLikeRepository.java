package com.ncsmap.community.like.repository;

import com.ncsmap.community.like.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            delete from PostLike pl
            where pl.post.id = :postId and pl.member.id = :memberId
            """)
    int deleteByPostIdAndMemberId(
            @Param("postId") Long postId,
            @Param("memberId") Long memberId
    );
}
