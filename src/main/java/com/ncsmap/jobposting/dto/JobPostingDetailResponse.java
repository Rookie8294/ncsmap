package com.ncsmap.jobposting.dto;

import com.ncsmap.jobposting.entity.JobPosting;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class JobPostingDetailResponse {

    private Long id;
    private String institution;
    private String sourcePostingId;
    private String title;
    private String hireType;
    private String recruitType;
    private String eduReq;
    private String workRegion;
    private Integer recruitCount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean ncsYn;
    private String ncsCodes;
    private String ncsCodeNames;
    private String status;
    private String sourceUrl;
    private String sourceType;
    private String applyQualification;
    private String disqualifyReason;
    private String processDesc;
    private String preferential;
    private String preferCondition;

    public static JobPostingDetailResponse of (JobPosting jobPosting){
        return JobPostingDetailResponse.builder()
                .id(jobPosting.getId())
                .institution(jobPosting.getInstitution().getName())
                .title(jobPosting.getTitle())
                .hireType(jobPosting.getHireType())
                .recruitType(jobPosting.getRecruitType())
                .eduReq(jobPosting.getEduReq())
                .workRegion(jobPosting.getWorkRegion())
                .recruitCount(jobPosting.getRecruitCount())
                .startDate(jobPosting.getStartDate())
                .endDate(jobPosting.getEndDate())
                .ncsYn(jobPosting.getNcsYn())
                .ncsCodes(jobPosting.getNcsCodes())
                .ncsCodeNames(jobPosting.getNcsCodeNames())
                .status(jobPosting.getStatus())
                .sourceUrl(jobPosting.getSourceUrl())
                .applyQualification(jobPosting.getApplyQualification())
                .disqualifyReason(jobPosting.getDisqualifyReason())
                .processDesc(jobPosting.getProcessDesc())
                .preferential(jobPosting.getPreferential())
                .preferCondition(jobPosting.getPreferCondition())
                .build();

    }
}
