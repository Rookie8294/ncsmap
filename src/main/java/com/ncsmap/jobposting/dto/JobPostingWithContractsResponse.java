package com.ncsmap.jobposting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Builder
public class JobPostingWithContractsResponse {

    private Long jobPostingId;
    private String jobPostingTitle;
    private String hireType;
    private String recruitType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String sourceUrl;
    private String processDesc;
    private String institutionName;
    private String institutionSiteUrl;

    private List<CandidateContract> candidateContracts;

    @Getter
    @Builder
    public static class CandidateContract {
        private Long contractId;
        private String title;
        private LocalDate contractDate;
        private Long totalContractAmount;
        private String businessType;
        private String contractMethod;
        private String contractInfoUrl;
        private String institutionCode;
        private String agencyName;
    }
}
