package com.ncsmap.jobposting.dto;

import java.time.LocalDate;

public record JobPostingSearchRequest(

        /* 검색 조건 */
        String title,                // 공고명
        String hireType,             // 고용형태
        String recruitType,          // 채용구분
        String eduReq,               // 학력조건
        String workRegion,           // 근무지역
        LocalDate startDate,         // 채용 시작일
        LocalDate endDate,           // 채용 종료일
        Boolean ncsYn,               // NCS 시험 여부
        String status,               // 채용공고 상태
        Integer page,
        Integer size

) {
}
