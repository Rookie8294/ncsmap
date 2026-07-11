package com.ncsmap.sync.client;


import com.ncsmap.sync.dto.JobPostingSyncCondition;
import com.ncsmap.sync.dto.PublicDataJobPostingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublicDataJobPostingClient {

    private final RestTemplate restTemplate;

    @Value("${public.data.api.key}")
    private String apiKey;

    @Value("${public.data.job-posting.url}")
    private String jobPostingUrl;

    public PublicDataJobPostingResponse fetch(
            Integer pageNo,
            Integer numOfRows,
            JobPostingSyncCondition condition) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(jobPostingUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("resultType", "json")
                .queryParam("pageNo", String.valueOf(pageNo))
                .queryParam("numOfRows", String.valueOf(numOfRows));

        //공고진행여부
        String ongoingYn = StringUtils.hasText(condition.ongoingYn()) ? condition.ongoingYn() : "Y";
        builder.queryParam("ongoingYn", ongoingYn);
        addIfPresent(builder, "pbancBgngYmd", condition.startDate());  // 채용공고 시작일
        addIfPresent(builder, "pbancEndYmd", condition.endDate());     // 채용공고 마감일

        String url = builder.build().toUriString();

        log.info("채용공고 API 호출 - 검색조건 : {}", condition);

        try{
            return restTemplate.getForObject(url, PublicDataJobPostingResponse.class);
        } catch (Exception e){
            log.error("채용공고 API 호출 실패 - 검색조건 : {}", condition);
            return null;
        }

    }

    private void addIfPresent(UriComponentsBuilder builder, String key, String value) {
        if (StringUtils.hasText(value)){
            builder.queryParam(key, value);
        }
    }

}
