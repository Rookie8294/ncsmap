package com.ncsmap.community.post.entity;

import com.ncsmap.institution.entity.Institution;
import com.ncsmap.jobposting.entity.JobPosting;
import com.ncsmap.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Getter
@Entity
@DynamicUpdate
@Table(
        name = "post",
        indexes = {
                @Index(
                        name = "idx_post_board_type_status_created_at",
                        columnList = "board_type, status, created_at"
                ),
                @Index(
                        name = "idx_post_member_status_created_at",
                        columnList = "member_id, status, created_at"
                ),
                @Index(
                        name = "idx_post_job_posting_status_created_at",
                        columnList = "job_posting_id, status, created_at"
                ),
                @Index(
                        name = "idx_post_institution_status_created_at",
                        columnList = "institution_id, status, created_at"
                )
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
    @Column(name = "board_type", nullable = false, length = 30)
    @Comment("게시판 유형")
    private BoardType boardType;

    @Column(nullable = false, length = 200)
    @Comment("게시글 제목")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("게시글 내용")
    private String content;

    @Column(name = "view_count", nullable = false)
    @Comment("조회수")
    private int viewCount;

    @Column(name = "like_count", nullable = false)
    @Comment("좋아요 수")
    private int likeCount;

    @Column(name = "comment_count", nullable = false)
    @Comment("댓글 수")
    private int commentCount;

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

    private Post(
            Member member,
            JobPosting jobPosting,
            Institution institution,
            BoardType boardType,
            String title,
            String content
    ) {
        validateBoardRelation(boardType, jobPosting, institution);

        this.member = member;
        this.jobPosting = jobPosting;
        this.institution = institution;
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        this.viewCount = 0;
        this.likeCount = 0;
        this.commentCount = 0;
        this.status = PostStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 게시글을 생성한다.
     */
    public static Post createPost(
            Member member,
            JobPosting jobPosting,
            Institution institution,
            BoardType boardType,
            String title,
            String content
    ) {
        return new Post(
                member,
                jobPosting,
                institution,
                boardType,
                title,
                content
        );
    }

    /**
     * 게시글의 게시판 유형, 제목, 내용을 수정한다.
     */
    public void update(
            BoardType boardType,
            JobPosting jobPosting,
            Institution institution,
            String title,
            String content
    ) {
        validateBoardRelation(boardType, jobPosting, institution);

        this.boardType = boardType;
        this.jobPosting = jobPosting;
        this.institution = institution;
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 게시판 유형과 연결 대상의 조합이 올바른지 검증한다.
     */
    private static void validateBoardRelation(
            BoardType boardType,
            JobPosting jobPosting,
            Institution institution
    ) {
        if (boardType == null) {
            throw new IllegalArgumentException("게시판 유형은 필수입니다.");
        }

        switch (boardType) {
            case FREE, STUDY -> {
                if (jobPosting != null || institution != null) {
                    throw new IllegalArgumentException(
                            "자유게시판과 스터디 게시판은 채용공고 또는 기관을 연결할 수 없습니다."
                    );
                }
            }

            case JOB_POSTING -> {
                if (jobPosting == null) {
                    throw new IllegalArgumentException(
                            "채용공고 게시판은 채용공고가 필수입니다."
                    );
                }

                if (institution != null) {
                    throw new IllegalArgumentException(
                            "채용공고 게시판에는 기관을 직접 연결할 수 없습니다."
                    );
                }
            }

            case INSTITUTION -> {
                if (institution == null) {
                    throw new IllegalArgumentException(
                            "기관 게시판은 기관이 필수입니다."
                    );
                }

                if (jobPosting != null) {
                    throw new IllegalArgumentException(
                            "기관 게시판에는 채용공고를 연결할 수 없습니다."
                    );
                }
            }
        }
    }

    /**
     * 게시글을 삭제 상태로 변경한다.
     */
    public void delete() {
        this.status = PostStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }
}
