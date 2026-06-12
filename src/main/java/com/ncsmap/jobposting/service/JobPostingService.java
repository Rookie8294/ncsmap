package com.ncsmap.jobposting.service;

import com.ncsmap.jobposting.dto.JobPostingDetailResponse;
import com.ncsmap.jobposting.dto.JobPostingListResponse;
import com.ncsmap.jobposting.dto.JobPostingSearchRequest;
import com.ncsmap.jobposting.entity.JobPosting;
import com.ncsmap.jobposting.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;

    // 채용공고 목록 조회
    public Page<JobPostingListResponse> getJobPostings(
            JobPostingSearchRequest request, Pageable pageable){

        return jobPostingRepository.search(request, pageable)
                .map(JobPostingListResponse::of);
    }

    // 채용공고 단건 조회
    public JobPostingDetailResponse getJobPosting(Long id) {
        JobPosting jobPosting = jobPostingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 공고입니다."));
        return JobPostingDetailResponse.of(jobPosting);
    }

}
