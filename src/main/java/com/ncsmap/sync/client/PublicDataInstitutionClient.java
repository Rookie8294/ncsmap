package com.ncsmap.sync.client;


import com.ncsmap.sync.dto.PublicDataInstitutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataInstitutionClient {

    private final RestTemplate restTemplate;

    @Value("${public.data.api.key}")
    private String apiKey;

    @Value("${public.data.institution.url}")
    private String institutionUrl;

    public PublicDataInstitutionResponse fetch() {

        String url = UriComponentsBuilder.fromUriString(institutionUrl)
                .queryParam("serviceKey",  apiKey)
                .queryParam("type", "json")
                .queryParam("numOfRows", 999)
                .build()
                .toUriString();

        log.info("기관 정보 API 호출");

        try {
            return restTemplate.getForObject(url, PublicDataInstitutionResponse.class);

        } catch (Exception e){
            log.error("기관 정보 API 호툴 실패", e.getMessage());
            return null;
        }
    }

}
