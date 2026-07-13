package com.ncsmap.sync.service;

import com.ncsmap.institution.entity.Institution;
import com.ncsmap.institution.repository.InstitutionRepository;
import com.ncsmap.jobposting.entity.JobPosting;
import com.ncsmap.jobposting.repository.JobPostingRepository;
import com.ncsmap.sync.client.PublicDataJobPostingClient;
import com.ncsmap.sync.dto.JobPostingSyncCondition;
import com.ncsmap.sync.dto.PublicDataJobPostingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingSyncService {

    private final PublicDataJobPostingClient jobPostingClient;
    private final JobPostingRepository jobPostingRepository;
    private final InstitutionRepository institutionRepository;

    private static final int NUM_OF_ROWS = 999;
    private static final int PAGE_NO = 1;
    private static final DateTimeFormatter DATE_FORMATTER =DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public void sync(JobPostingSyncCondition condition) {
        log.info("채용공고 동기화 시작");

        int pageNo = ( condition.pageNo() == null ) ?  PAGE_NO : condition.pageNo();
        int numOfRows = ( condition.numOfRows() == null ) ?  NUM_OF_ROWS : condition.numOfRows();
        int insertCount = 0;
        int updateCount = 0;
        int skipCount = 0;
        int totalProcessed = 0;
        Integer totalCount = null;

        while (true) {

            // 1. API 호출
            PublicDataJobPostingResponse response = jobPostingClient.fetch(pageNo, numOfRows, condition);

            // 2. 응답 검증
            if (response == null
                    || response.getResult() == null
                    || response.getResult().isEmpty()) {
                log.info("데이터 없음");
                break;
            }

            // 첫 응답에서 totalCount 확인
            if ( totalCount == null) {
                totalCount = response.getTotalCount();

                // totalCount가 없거나 0이면 즉시 종료
                if (totalCount == null || totalCount == 0){
                    log.info("totalCount 응답값 : {}, 동기화 중단", totalCount);
                }
            }


            List<PublicDataJobPostingResponse.JobPosting> list = response.getResult();

            log.info("조회 리스트 사이즈 : {}", list.size());
            // 3. Upsert
            for (PublicDataJobPostingResponse.JobPosting dto :  list){
                // 기관 매핑 (institutionCode로 조회)
                Institution institution = institutionRepository
                        .findByCode(dto.getInstitutionCode())
                        .orElse(null);

                if (institution == null) {
                    skipCount++;
                    log.warn("기관 매핑 실패 - 기관 코드 : {}, 공고 : {}", dto.getInstitutionCode(), dto.getTitle());
                    continue;
                }

                int result = upsert(dto, institution);
                if (result == 1) insertCount++;
                if (result == 2) updateCount++;
            }

            totalProcessed += list.size();

            log.info("채용공고 동기화 진행중 - page : {}, 신규 : {}, 갱신 : {}, 스킵 : {}",
                    pageNo, insertCount, updateCount, skipCount);


            log.info("루프 조건 totalProcessed : {} | totalCount : {}", totalProcessed, totalCount);
            // 4. 마지막 페이지 확인
            if (totalProcessed >= totalCount) {
                break;
            }

            pageNo++;
        }

        log.info("채용공고 동기화 완료 - 신규 : {}건, 갱신 : {}건, 스킵 : {}건",
                insertCount, updateCount, skipCount);
    }

    private int upsert(PublicDataJobPostingResponse.JobPosting dto, Institution institution) {

        JobPosting jobPosting = jobPostingRepository
                .findBySourcePostingId(dto.getSourcePostingId())
                .orElse(null);

        if (jobPosting == null) {
            // Insert
            jobPostingRepository.save(
                    JobPosting.builder()
                            .institution(institution)
                            .sourcePostingId(dto.getSourcePostingId())
                            .title(dto.getTitle())
                            .hireType(dto.getHireType())
                            .recruitType(dto.getRecruitType())
                            .eduReq(dto.getEduReq())
                            .workRegion(dto.getWorkRegion())
                            .recruitCount(dto.getRecruitCount())
                            .startDate(parseDate(dto.getStartDate()))
                            .endDate(parseDate(dto.getEndDate()))
                            // ncsYn ncs시험여부 수정 필요
                            .ncsYn(hasNcs(dto.getNcsCodes()))
                            .ncsCodes(dto.getNcsCodes())
                            .ncsCodeNames(dto.getNcsCodeNames())
                            .status(parseStatus(dto.getOngoingYn()))
                            .sourceUrl(dto.getSourceUrl())
                            .sourceType("PUBLIC_DATA")
                            .applyQualification(dto.getApplyQualification())
                            .disqualifyReason(dto.getDisqualifyReason())
                            .processDesc(dto.getProcessDesc())
                            .preferential(dto.getPreferential())
                            .preferCondition(dto.getPreferCondition())
                            .build()
            );
            return 1;
        } else {
            // UPDATE
            jobPosting.update(
                    dto.getTitle(),
                    dto.getHireType(),
                    dto.getRecruitType(),
                    dto.getEduReq(),
                    dto.getWorkRegion(),
                    dto.getRecruitCount(),
                    parseDate(dto.getStartDate()),
                    parseDate(dto.getEndDate()),
                    hasNcs(dto.getNcsCodes()),
                    dto.getNcsCodes(),
                    dto.getNcsCodeNames(),
                    parseStatus(dto.getOngoingYn()),
                    dto.getSourceUrl(),
                    dto.getApplyQualification(),
                    dto.getDisqualifyReason(),
                    dto.getProcessDesc(),
                    dto.getPreferential(),
                    dto.getPreferCondition()
            );
            return 2;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패 : {}", dateStr);
            return null;
        }
    }

    private String parseStatus(String ongoingYn) {
        if ("Y".equals(ongoingYn)) return "진행중";
        if ("N".equals(ongoingYn)) return "마감";
        return "마감"; // null이면 마감
    }

    private Boolean hasNcs(String ncsCodes) {
        return ncsCodes != null && !ncsCodes.isBlank();
    }

}
