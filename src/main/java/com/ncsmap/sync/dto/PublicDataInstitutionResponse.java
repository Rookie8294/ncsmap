package com.ncsmap.sync.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PublicDataInstitutionResponse {

    private Integer resultCode;
    private String resultMsg;
    private Integer totalCount;
    private List<Institution> result;

    @Getter
    @NoArgsConstructor
    public static class Institution {

        @JsonProperty("instCd")
        private String code;

        @JsonProperty("instNm")
        private String name;

        @JsonProperty("instTypeNm")
        private String type;

        @JsonProperty("sprvsnInstNm")
        private String ministry;

        @JsonProperty("roadNmAddr")
        private String roadAddress;

        @JsonProperty("siteUrl")
        private String siteUrl;

    }
}
