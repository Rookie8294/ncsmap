package com.ncsmap.sync.service;


import com.ncsmap.institution.entity.Institution;
import com.ncsmap.institution.repository.InstitutionRepository;
import com.ncsmap.sync.client.PublicDataInstitutionClient;
import com.ncsmap.sync.dto.PublicDataInstitutionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstitutionSyncService {

    private final PublicDataInstitutionClient institutionClient;
    private final InstitutionRepository institutionRepository;

    public void sync() {

        log.info("기관 정보 동기화 시작");

        // 1. 전체 테이터 한번에 호출
        PublicDataInstitutionResponse response = institutionClient.fetch();

        if (response == null
                || response.getResult() == null
                || response.getResult().isEmpty()) {
            log.warn("기관 정보 API 응답 없음");
            return;
        }

        List<PublicDataInstitutionResponse.Institution> list = response.getResult();

        // 2. 기관 Upsert
        int insertCount = 0;
        int updateCount = 0;

        for (PublicDataInstitutionResponse.Institution dto : list) {
            boolean isNew = upsert(dto);
            if (isNew) insertCount++;
            else updateCount++;
        }

        log.info("기관 정보 동기화 완료 - 신규 : {}건, 갱신 : {}건, 총 : {}건",
                insertCount, updateCount, list.size());

    }

    private boolean upsert(PublicDataInstitutionResponse.Institution dto) {
        Institution institution = institutionRepository
                .findByCode(dto.getCode())
                .orElse(null);

        if (institution == null) {
            // Insert
            institutionRepository.save(
                    Institution.builder()
                            .code(dto.getCode())
                            .name(dto.getName())
                            .type(dto.getType())
                            .ministry(dto.getMinistry())
                            .roadAddress(dto.getRoadAddress())
                            .siteUrl(dto.getSiteUrl())
                            .build()
            );
            return true;
        } else {
            // Update
            institution.update(
                    dto.getName(),
                    dto.getType(),
                    dto.getMinistry(),
                    dto.getRoadAddress(),
                    dto.getType()
            );
            return false;
        }
    }

}
