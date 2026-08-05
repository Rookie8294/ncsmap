package com.ncsmap.sync.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PublicDataContractResponse {

    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Header header;
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {
        private Integer numOfRows;
        private Integer pageNo;
        private Integer totalCount;
        private List<Contract> items;
    }

    @Getter
    @NoArgsConstructor
    public static class Contract {

        @JsonProperty("cntrctNo")
        private String contractNumber;

        @JsonProperty("untyCntrctNo")
        private String unifiedContractNumber;

        @JsonProperty("cntrctNm")
        private String title;

        @JsonProperty("bsnsDivNm")
        private String businessType;

        @JsonProperty("cntrctCnclsMthdNm")
        private String contractMethod;

        @JsonProperty("cntrctCnclsDate")
        private String contractDate;

        @JsonProperty("cntrctAmt")
        private String contractAmount;

        @JsonProperty("ttalCntrctAmt")
        private String totalContractAmount;

        @JsonProperty("cntrctInfoUrl")
        private String contractInfoUrl;

        @JsonProperty("bidNtceNo")
        private String bidNoticeNumber;

        @JsonProperty("bidNtceNm")
        private String bidNoticeName;

        @JsonProperty("dmndInsttDivNm")
        private String institutionDivision;

        @JsonProperty("dmndInsttCd")
        private String institutionCode;

        @JsonProperty("dmndInsttNm")
        private String institutionName;

        @JsonProperty("rprsntCorpNm")
        private String agencyName;

        @JsonProperty("rprsntCorpBizrno")
        private String businessNumber;

        @JsonProperty("rprsntCorpCeoNm")
        private String ceoName;

        @JsonProperty("rprsntCorpAdrs")
        private String address;
    }
}
