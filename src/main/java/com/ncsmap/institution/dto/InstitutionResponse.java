package com.ncsmap.institution.dto;

import com.ncsmap.institution.entity.Institution;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstitutionResponse {

    private Long id;
    private String name;
    private String type;
    private String code;
    private String ministry;
    private String loadAddress;
    private String siteUrl;

    // Entity -> DTO
    public static InstitutionResponse of(Institution institution) {
        return InstitutionResponse.builder()
                .id(institution.getId())
                .name(institution.getName())
                .type(institution.getType())
                .code(institution.getCode())
                .ministry(institution.getMinistry())
                .loadAddress(institution.getLoadAddress())
                .siteUrl(institution.getSiteUrl())
                .build();
    }
}
