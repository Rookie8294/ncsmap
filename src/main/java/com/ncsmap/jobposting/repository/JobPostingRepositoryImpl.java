package com.ncsmap.jobposting.repository;

import com.ncsmap.jobposting.dto.JobPostingSearchRequest;
import com.ncsmap.jobposting.entity.JobPosting;
import com.ncsmap.jobposting.entity.QJobPosting;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class JobPostingRepositoryImpl implements JobPostingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<JobPosting> search(JobPostingSearchRequest request, Pageable pageable) {

        QJobPosting jobPosting = QJobPosting.jobPosting;
        BooleanBuilder builder = new BooleanBuilder();

        // 1. 공고명 ( contains = like )
        if (hasText(request.title())) {
            builder.and(jobPosting.title.contains(request.title()));
        }
        // 2. 공고형태
        if (hasText(request.hireType())){
            builder.and(jobPosting.hireType.eq(request.hireType()));
        }
        // 3. 채용구분
        if (hasText(request.recruitType())){
            builder.and(jobPosting.recruitType.eq(request.recruitType()));
        }
        // 4. 학력조건
        if (hasText(request.hireType())){
            builder.and(jobPosting.eduReq.eq(request.eduReq()));
        }
        // 5. 근무지역
        if (hasText(request.workRegion())){
            builder.and(jobPosting.workRegion.eq(request.workRegion()));
        }
        // 6. NCS 여부
        if (request.ncsYn() != null){
            builder.and(jobPosting.ncsYn.eq(request.ncsYn()));
        }
        // 7. 상태
        if (hasText(request.status())){
            builder.and(jobPosting.status.eq(request.status()));
        }
        // 8. 시작 날짜
        if (request.startDate() != null){
            builder.and(jobPosting.endDate.goe(request.startDate()));
        }
        // 9. 마감 날짜
        if (request.endDate() != null){
            builder.and(jobPosting.startDate.loe(request.endDate()));
        }

        // 데이터 조회
        List<JobPosting> content = queryFactory
                .selectFrom(jobPosting)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수
        Long total = queryFactory
                .select(jobPosting.count())
                .from(jobPosting)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);

    }

    private boolean hasText(String str){
        return str != null && !str.isBlank();
    }
}