package com.ncsmap.sync.service;


import com.ncsmap.agency.entity.Agency;
import com.ncsmap.agency.repository.AgencyRepository;
import com.ncsmap.contract.entity.Contract;
import com.ncsmap.contract.repository.ContractRepository;
import com.ncsmap.institution.entity.Institution;
import com.ncsmap.institution.repository.InstitutionRepository;
import com.ncsmap.sync.client.PublicDataContractClient;
import com.ncsmap.sync.dto.PublicDataContractResponse;
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
public class ContractSyncService {

    private final PublicDataContractClient contractClient;
    private final ContractRepository contractRepository;
    private final AgencyRepository agencyRepository;
    private final InstitutionRepository institutionRepository;

    private static final int PERIOD_DAYS = 7;
    private static final int NUM_OF_ROWS = 999;
    private static final int MAX_PAGE = 500;

    private static final DateTimeFormatter API_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyMMdd");

    private static final List<String> BUSINESS_TYPE = List.of(
            "용역",
            "물품"
    );

    private static final List<String> RECRUITMENT_KEYWORDS = List.of(
            "채용",
            "인재영입",
            "신입사원 선발"
    );

    private static final List<String> EXCLUDED_KEYWORDS = List.of(
            "레지던트",
            "전문의",
            "치과의사",
            "채용박람회",
            "채용엑스포"
    );

    @Transactional
    public void sync(String startDate, String endDate) {

        log.info("계약정보 동기화 시작 - 전체 기간 : {} ~ {}", startDate, endDate);

        LocalDate current = LocalDate.parse(startDate, API_DATE_FORMAT);
        LocalDate finalEnd = LocalDate.parse(endDate, API_DATE_FORMAT);

        int totalInsert = 0;
        int totalSkip = 0;
        int totalFilteredOut = 0;
        int totalMatchedInstitution = 0;

        while (!current.isAfter(finalEnd)) {

            LocalDate periodEnd = current.plusDays(PERIOD_DAYS - 1);
            if (periodEnd.isAfter(finalEnd)){
                periodEnd = finalEnd;
            }

            String bgnStr = current.format(API_DATE_FORMAT);
            String endStr = periodEnd.format(API_DATE_FORMAT);

            SyncResult result = syncPeriod(bgnStr, endStr);
            totalInsert += result.insertCount();
            totalSkip += result.skipCount();
            totalFilteredOut += result.filteredOutCount();
            totalMatchedInstitution += result.matchedInstitutionCount();

            current = periodEnd.plusDays(1);
        }

        log.info("계약정보 동기화 완료 - 신규: {}건 (기관 매칭: {}건), 스킵: {}건, 필터링 제외: {}건",
                totalInsert, totalMatchedInstitution, totalSkip, totalFilteredOut);
    }

    private SyncResult syncPeriod(String bgnDate, String endDate) {

        int pageNo = 1;
        int insertCount = 0;
        int skipCount = 0;
        int filteredOutCount = 0;
        int matchedInstitutionCount = 0;
        int totalProcessed = 0;
        Integer totalCount = null;

        while (pageNo <= MAX_PAGE) {

            PublicDataContractResponse response =
                    contractClient.fetch(pageNo, NUM_OF_ROWS, bgnDate, endDate);

            if (response == null
                    || response.getResponse() == null
                    || response.getResponse().getBody() == null
                    || response.getResponse().getBody().getItems() == null
                    || response.getResponse().getBody().getItems().isEmpty()) {
                log.info("데이터 없음 - 기간: {}~{}, pageNo: {}", bgnDate, endDate, pageNo);
                break;
            }

            if (totalCount == null) {
                totalCount = response.getResponse().getBody().getTotalCount();
                if (totalCount == null || totalCount == 0) {
                    log.warn("totalCount 비정상 - 기간: {}~{}, 종료", bgnDate, endDate);
                    break;
                }
            }

            List<PublicDataContractResponse.Contract> items =
                    response.getResponse().getBody().getItems();

            for (PublicDataContractResponse.Contract dto : items) {

                // 1. 용역, 물품 계약 필터링
                if (!isBusinessType(dto.getBusinessType())) {
                    filteredOutCount++;
                    continue;
                }

                // 2. 제외 단어 필터링
                if (isExcludedKeyword(dto.getTitle())){
                    filteredOutCount++;
                    continue;
                }

                // 3. 채용 관련 계약 필터링
                if (!isRecruitmentContract(dto.getTitle())) {
                    filteredOutCount++;
                    continue;
                }

                // 4. 중복 계약 체크
                if (contractRepository.findByContractNumber(dto.getContractNumber()).isPresent()) {
                    skipCount++;
                    continue;
                }

                // 5. naraCode로 조회 ( 계약 정보 수요기관 코드 )
                Institution institution = institutionRepository
                        .findByNaraCode(dto.getInstitutionCode())
                        .orElse(null);


                log.warn("[Contract Sync] - institutionCode: {}, institutionName: {}", dto.getInstitutionCode(), dto.getInstitutionName());

                // 6. naraCode로 조회된 기관 없다면 이름으로 조회 후 naraCode 업데이트
                if ( institution == null) {

                    institution = institutionRepository
                            .findByName(dto.getInstitutionName().replace(" ", ""))
                            .orElse(null);

                    if ( institution != null && institution.getNaraCode() == null) {
                        institution.updateNaraCode(dto.getInstitutionCode());
                    }

                }

                // 7. 기관 매칭 카운팅
                if (institution != null) {
                    matchedInstitutionCount++;
                }

                // 8. Agency 조회 or 생성
                Agency agency = getOrCreateAgency(dto);

                // 9. Contract 저장
                try {
                    contractRepository.save(
                            Contract.builder()
                                    .institution(institution)
                                    .institutionCode(dto.getInstitutionCode())
                                    .institutionName(dto.getInstitutionName())
                                    .institutionDivision(dto.getInstitutionDivision())
                                    .agency(agency)
                                    .contractNumber(dto.getContractNumber())
                                    .unifiedContractNumber(dto.getUnifiedContractNumber())
                                    .title(dto.getTitle())
                                    .contractMethod(dto.getContractMethod())
                                    .businessType(dto.getBusinessType())
                                    .contractDate(parseDate(dto.getContractDate()))
                                    .contractAmount(parseLong(dto.getContractAmount()))
                                    .totalContractAmount(parseLong(dto.getTotalContractAmount()))
                                    .bidNoticeNumber(dto.getBidNoticeNumber())
                                    .bidNoticeName(dto.getBidNoticeName())
                                    .contractInfoUrl(dto.getContractInfoUrl())
                                    .build()
                    );
                    insertCount++;
                } catch (Exception e) {
                    log.error("계약 저장 실패 - contractNumber: {}, error: {}",
                            dto.getContractNumber(), e.getMessage());
                    skipCount++;
                }
            }

            totalProcessed += items.size();
            log.info("계약 동기화 진행중 - 기간: {}~{}, page: {}, 처리: {}/{}",
                    bgnDate, endDate, pageNo, totalProcessed, totalCount);

            if (totalProcessed >= totalCount) {
                break;
            }

            pageNo++;
        }

        if (pageNo > MAX_PAGE) {
            log.error("페이지네이션 비정상 종료 - 기간: {}~{}, MAX_PAGE({}) 초과",
                    bgnDate, endDate, MAX_PAGE);
        }

        return new SyncResult(insertCount, skipCount, filteredOutCount, matchedInstitutionCount);
    }

    private Agency getOrCreateAgency(PublicDataContractResponse.Contract dto) {
        return agencyRepository.findByBusinessNumber(dto.getBusinessNumber())
                .orElseGet(() -> agencyRepository.save(
                        Agency.builder()
                                .agencyName(dto.getAgencyName())
                                .businessNumber(dto.getBusinessNumber())
                                .ceoName(dto.getCeoName())
                                .address(dto.getAddress())
                                .build()
                ));
    }
    private boolean isRecruitmentContract(String title) {
        if (title == null) return false;
        String normalized = title.replace(" ", "");
        return RECRUITMENT_KEYWORDS.stream().anyMatch(normalized::contains);
    }

    private boolean isBusinessType(String businessType) {
        return BUSINESS_TYPE.stream().anyMatch(businessType::contains);
    }

    private boolean isExcludedKeyword(String keyword) {
        return EXCLUDED_KEYWORDS.stream().anyMatch(keyword::contains);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", dateStr);
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    private record SyncResult(int insertCount, int skipCount, int filteredOutCount,
                              int matchedInstitutionCount) {}
}
