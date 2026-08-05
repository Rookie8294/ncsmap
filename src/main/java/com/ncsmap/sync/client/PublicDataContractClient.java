package com.ncsmap.sync.client;

import com.ncsmap.sync.dto.PublicDataContractResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataContractClient {

    private final RestTemplate restTemplate;

    @Value("${public.data.api.key}")
    private String apiKey;

    @Value("${public.data.contract.url}")
    private String contractUrl;

    public PublicDataContractResponse fetch(int pageNo, int numOfRows,
                                          String cntrctCnclsBgnDate,
                                          String cntrctCnclsEndDate){
        String url = UriComponentsBuilder.fromUriString(contractUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("type", "json")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("cntrctCnclsBgnDate", cntrctCnclsBgnDate)
                .queryParam("cntrctCnclsEndDate", cntrctCnclsEndDate)
                .build()
                .toUriString();

        log.info("계약정보 API 호출 - pageNo: {}, 기간 : {} ~ {}, url : {} ",
                pageNo, cntrctCnclsBgnDate, cntrctCnclsEndDate, url);
        try {
            return restTemplate.getForObject(url, PublicDataContractResponse.class);
        } catch (Exception e) {
            log.error("계약정보 API 호출 실패 - error: {}", e.getMessage());
            return null;
        }

    }


}
