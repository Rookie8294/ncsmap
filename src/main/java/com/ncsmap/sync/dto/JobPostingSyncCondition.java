package com.ncsmap.sync.dto;

public record JobPostingSyncCondition(
        Integer pageNo,       // 페이지 번호
        Integer numOfRows,    // 페이지당 데이터 수
        String ongoingYn,     // 진행여부
        String startDate,     // 채용공고 시작일 pbancBgngYmd
        String endDate        // 채용공고 마감일 pbancEndYmd
) {}
