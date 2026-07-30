package com.ncsmap.jobposting.service;


import com.ncsmap.contract.entity.Contract;
import com.ncsmap.contract.repository.ContractRepository;
import com.ncsmap.jobposting.dto.JobPostingWithContractsResponse;
import com.ncsmap.jobposting.dto.UnmappedJobPostingProjection;
import com.ncsmap.jobposting.entity.JobPosting;
import com.ncsmap.jobposting.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPostingMappingService {

    private final JobPostingRepository jobPostingRepository;
    private final ContractRepository contractRepository;

    @Transactional
    public void mapContractToJobPosting(Long jobPostingId, Long contractId) {

        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("공고 없음 : " + jobPostingId));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("계약 없음 : " + contractId));

        jobPosting.updateContract(contract);

        log.info("[Mapping] 수동 매핑 확인 - jobPostingId: {}, contractId: {}", jobPostingId, contractId);
    }

    public List<JobPostingWithContractsResponse> getJobUnmappedJobPostings(){
        List<UnmappedJobPostingProjection> results = jobPostingRepository.findUnmappedJobPostings();

        // 공고 id 기준으로 그룹화 [ ex - 공고1 : 계약A, 계약B, 계약C / 공고2 : 계약D, 계약E ]
        Map<Long, List<UnmappedJobPostingProjection>> grouped = results.stream()
                .collect(Collectors.groupingBy(
                        UnmappedJobPostingProjection::getJobPostingId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return grouped.values().stream()
                .map(rows -> {
                    UnmappedJobPostingProjection first = rows.get(0);

                    List<JobPostingWithContractsResponse.CandidateContract> candidates = rows.stream()
                            .map(row -> JobPostingWithContractsResponse.CandidateContract.builder()
                                    .contractId(row.getContractId())
                                    .title(row.getContractTitle())
                                    .contractDate(row.getContractDate())
                                    .totalContractAmount(row.getTotalContractAmount())
                                    .businessType(row.getBusinessType())
                                    .contractMethod(row.getContractMethod())
                                    .contractInfoUrl(row.getContractInfoUrl())
                                    .institutionCode(row.getInstitutionCode())
                                    .agencyName(row.getAgencyName())
                                    .build())
                            .toList();

                    return JobPostingWithContractsResponse.builder()
                            .jobPostingId(first.getJobPostingId())
                            .jobPostingTitle(first.getJobPostingTitle())
                            .hireType(first.getHireType())
                            .recruitType(first.getRecruitType())
                            .startDate(first.getStartDate())
                            .endDate(first.getEndDate())
                            .status(first.getStatus())
                            .sourceUrl(first.getSourceUrl())
                            .processDesc(first.getProcessDesc())
                            .institutionName(first.getInstitutionName())
                            .institutionSiteUrl(first.getInstitutionSiteUrl())
                            .candidateContracts(candidates)
                            .build();

                })
                .toList();

    }


}
