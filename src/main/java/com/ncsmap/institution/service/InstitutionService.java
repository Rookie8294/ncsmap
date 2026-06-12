package com.ncsmap.institution.service;

import com.ncsmap.institution.dto.InstitutionResponse;
import com.ncsmap.institution.entity.Institution;
import com.ncsmap.institution.repository.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    // 기관 전체 조회
    public Page<InstitutionResponse> getInstitutions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return institutionRepository.findAll(pageable)
                .map(InstitutionResponse::of);

    }

    // 기관 단일 조회
    public InstitutionResponse getInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("존재하지 않는 기관입니다."));

        return InstitutionResponse.of(institution);
    }

}
