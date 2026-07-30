package com.ncsmap.community.post.repository;

import com.ncsmap.community.post.dto.PostSearchCondition;
import com.ncsmap.community.post.entity.BoardType;
import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.PostStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.ncsmap.community.post.entity.QPost.post;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    // 검색 조건에 맞는 게시글 목록 페이징 조회
    @Override
    public Page<Post> searchPost(PostSearchCondition postSearchCondition, Pageable pageable) {

        log.info(
                "게시글 QueryDSL 조회 boardType={}, jobPostingId={}, institutionId={}, keyword={}, page={}, size={}",
                postSearchCondition.getBoardType(),
                postSearchCondition.getJobPostingId(),
                postSearchCondition.getInstitutionId(),
                postSearchCondition.getKeyword(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        validateSearchCondition(postSearchCondition);

        List<Post> content = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.member).fetchJoin()
                .leftJoin(post.jobPosting).fetchJoin()
                .leftJoin(post.institution).fetchJoin()
                .where(
                        post.status.eq(PostStatus.ACTIVE),
                        boardTypeEq(postSearchCondition.getBoardType()),
                        jobPostingIdEq(postSearchCondition.getJobPostingId()),
                        institutionIdEq(postSearchCondition.getInstitutionId()),
                        keywordContains(postSearchCondition.getKeyword())
                )
                .orderBy(post.createdAt.desc(), post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(
                        post.status.eq(PostStatus.ACTIVE),
                        boardTypeEq(postSearchCondition.getBoardType()),
                        jobPostingIdEq(postSearchCondition.getJobPostingId()),
                        institutionIdEq(postSearchCondition.getInstitutionId()),
                        keywordContains(postSearchCondition.getKeyword())
                );

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> {
                    Long totalCount = countQuery.fetchOne();
                    return totalCount != null ? totalCount : 0L;
                }
        );
    }

    // 게시판 유형 검색 조건을 생성
    private BooleanExpression boardTypeEq(BoardType boardType) {
        return boardType != null
                ? post.boardType.eq(boardType)
                : null;
    }

    // 채용공고 ID 검색 조건을 생성
    private BooleanExpression jobPostingIdEq(Long jobPostingId) {
        return jobPostingId != null
                ? post.jobPosting.id.eq(jobPostingId)
                : null;
    }

    // 기관 ID 검색 조건을 생성
    private BooleanExpression institutionIdEq(Long institutionId) {
        return institutionId != null
                ? post.institution.id.eq(institutionId)
                : null;
    }

    // 제목 또는 본문 검색 조건을 생성
    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String trimmedKeyword = keyword.trim();

        return post.title.containsIgnoreCase(trimmedKeyword)
                .or(post.content.containsIgnoreCase(trimmedKeyword));
    }

    // 게시판 유형과 연관 ID 검색 조건의 조합을 검증
    private void validateSearchCondition(PostSearchCondition condition) {
        BoardType boardType = condition.getBoardType();
        Long jobPostingId = condition.getJobPostingId();
        Long institutionId = condition.getInstitutionId();

        if (boardType == null) {
            return;
        }

        switch (boardType) {
            case FREE, STUDY -> {
                if (jobPostingId != null || institutionId != null) {
                    throw new IllegalArgumentException(
                            "자유게시판과 스터디 게시판에는 채용공고 또는 기관 조건을 사용할 수 없습니다."
                    );
                }
            }

            case JOB_POSTING -> {
                if (institutionId != null) {
                    throw new IllegalArgumentException(
                            "채용공고 게시판 조회에는 기관 조건을 사용할 수 없습니다."
                    );
                }
            }

            case INSTITUTION -> {
                if (jobPostingId != null) {
                    throw new IllegalArgumentException(
                            "기관 게시판 조회에는 채용공고 조건을 사용할 수 없습니다."
                    );
                }
            }
        }
    }
}
