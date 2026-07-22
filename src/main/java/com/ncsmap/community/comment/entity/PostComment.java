package com.ncsmap.community.comment.entity;

import com.ncsmap.community.post.entity.Post;
import com.ncsmap.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "post_comment",
        indexes = {
                @Index(name = "idx_post_comment_post_created", columnList = "post_id, created_at"),
                @Index(name = "idx_post_comment_parent", columnList = "comment_parent_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("댓글 아이디")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @Comment("게시글")
    private Post post;

    /**
     * 부모 댓글
     * null이면 일반 댓글
     * 값이 있으면 대댓글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_parent_id", referencedColumnName = "id")
    @Comment("부모 댓글")
    private PostComment parent;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("내용")
    private String content;

    @Column(name = "is_deleted", nullable = false)
    @Comment("삭제 여부")
    private boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Comment("수정일")
    private LocalDateTime updatedAt;

    // 댓글 생성
    public PostComment(Member member, Post post, PostComment parent, String content) {
        this.member = member;
        this.post = post;
        this.parent = parent;
        this.content = content;
    }

    // 댓글 수정
    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 댓글 삭제(Soft Delete)
    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    // 생성일 설정
    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 수정일 갱신
    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
