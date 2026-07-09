package com.ncsmap.community.post.entity;

import com.ncsmap.institution.entity.Institution;
import com.ncsmap.jobposting.entity.JobPosting;
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
        name = "post",
        indexes = {
                @Index(name = "idx_post_member", columnList = "member_id"),
                @Index(name = "idx_post_institution", columnList = "institution_id"),
                @Index(name = "idx_post_job_posting", columnList = "job_posting_id"),
                @Index(name = "idx_post_category", columnList = "category_id"),
                @Index(name = "idx_post_created_at", columnList = "created_at")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Comment("커뮤니티 게시글")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("게시글 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("게시글 작성자")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id")
    @Comment("관련 채용공고")
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    @Comment("관련 기관")
    private Institution institution;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Comment("게시글 카테고리")
    private PostCategory category;

    @Column(nullable = false, length = 200)
    @Comment("게시글 제목")
    private String title;

    @Lob
    @Column(nullable = false)
    @Comment("게시글 내용")
    private String content;

    @Column(name = "view_count", nullable = false)
    @Comment("조회수")
    private int viewCount;

    @Column(name = "like_count", nullable = false)
    @Comment("좋아요 수")
    private int likeCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("게시글 상태")
    private PostStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("생성일시")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Comment("수정일시")
    private LocalDateTime updatedAt;

    private Post(Member member,
                 JobPosting jobPosting,
                 Institution institution,
                 PostCategory category,
                 String title,
                 String content) {

        this.member = member;
        this.jobPosting = jobPosting;
        this.institution = institution;
        this.category = category;
        this.title = title;
        this.content = content;
        this.viewCount = 0;
        this.likeCount = 0;
        this.status = PostStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    // 게시글 생성
    public static Post createPost(Member member,
                                  JobPosting jobPosting,
                                  Institution institution,
                                  PostCategory category,
                                  String title,
                                  String content) {

        return new Post(
                member,
                jobPosting,
                institution,
                category,
                title,
                content
        );
    }

    // 게시글 수정
    public void update(PostCategory category,
                       String title,
                       String content) {

        this.category = category;
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가
    public void increaseViewCount() {
        this.viewCount++;
    }

    // 좋아요 증가
    public void increaseLikeCount() {
        this.likeCount++;
    }

    // 좋아요 감소
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // 게시글 삭제
    public void delete() {
        this.status = PostStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }
}
