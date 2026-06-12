package com.ncsmap.jobposting.repository;

import com.ncsmap.jobposting.dto.JobPostingSearchRequest;
import com.ncsmap.jobposting.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobPostingRepositoryCustom {
    Page<JobPosting> search(JobPostingSearchRequest request, Pageable pageable);
}
