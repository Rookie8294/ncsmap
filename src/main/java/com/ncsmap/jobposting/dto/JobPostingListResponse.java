package com.ncsmap.jobposting.dto;


import com.ncsmap.jobposting.entity.JobPosting;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class JobPostingListResponse {

    /* 채용공고 목록 */
    private Long id;
    private String institutionName;         // 기관명
    private String title;                   // 공고 제목
    private String hireType;                // 고용형태
    private String recruitType;             // 채용구분
    private String workRegion;              // 근무지역
    private LocalDate startDate;            // 시작일
    private LocalDate endDate;              // 마감일
    private Boolean ncsYn;                  // NCS시험 여부
    private String status;                  // 상태

    public static JobPostingListResponse of(JobPosting jobPosting){
        return JobPostingListResponse.builder()
                .id(jobPosting.getId())
                .institutionName(jobPosting.getInstitution().getName())
                .title(jobPosting.getTitle())
                .hireType(jobPosting.getHireType())
                .recruitType(jobPosting.getRecruitType())
                .workRegion(jobPosting.getWorkRegion())
                .startDate(jobPosting.getStartDate())
                .endDate(jobPosting.getEndDate())
                .ncsYn(jobPosting.getNcsYn())
                .status(jobPosting.getStatus())
                .build();
    }

}
