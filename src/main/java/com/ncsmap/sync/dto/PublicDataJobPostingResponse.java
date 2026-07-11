package com.ncsmap.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PublicDataJobPostingResponse {

    private Integer resultCode;
    private String resultMsg;
    private Integer totalCount;
    private List<JobPosting> result;

    @Getter
    @NoArgsConstructor
    public static class JobPosting {

        @JsonProperty("recrutPblntSn")
        private String sourcePostingId;     // 공고 고유번호

        @JsonProperty("pblntInstCd")
        private String institutionCode;     // 기관 코드

        @JsonProperty("instNm")
        private String institutionName;     // 기관명

        @JsonProperty("recrutPbancTtl")
        private String title;               // 공고 제목

        @JsonProperty("hireTypeNmLst")
        private String hireType;            // 공고형태

        @JsonProperty("recrutSeNm")
        private String recruitType;         // 채용구분

        @JsonProperty("acbgCondNmLst")
        private String eduReq;              // 학력조건

        @JsonProperty("workRgnNmLst")
        private String workRegion;          // 근무지역

        @JsonProperty("recrutNope")
        private Integer recruitCount;       // 채용인원

        @JsonProperty("pbancBgngYmd")
        private String startDate;           // 시작일

        @JsonProperty("pbancEndYmd")
        private String endDate;             // 종료일

        @JsonProperty("ncsCdLst")
        private String ncsCodes;            // NCS 코드

        @JsonProperty("ncsCdNmLst")
        private String ncsCodeNames;        // NCS 분류명

        @JsonProperty("ongoingYn")
        private String ongoingYn;           // 진행여부 Y/N

        @JsonProperty("srcUrl")
        private String sourceUrl;           // 원본 URL

        @JsonProperty("aplyQlfcCn")
        private String applyQualification;  // 지원자격

        @JsonProperty("disqlfcRsn")
        private String disqualifyReason;    // 결격사유

        @JsonProperty("scrnprcdrMthdExpln")
        private String processDesc;         // 전형절차

        @JsonProperty("prefCn")
        private String preferential;        // 우대사항

        @JsonProperty("prefCondCn")
        private String preferCondition;     // 우대조건 요약
    }
}