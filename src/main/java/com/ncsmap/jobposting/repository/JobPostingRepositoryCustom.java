package com.ncsmap.jobposting.repository;

import com.ncsmap.jobposting.dto.JobPostingSearchRequest;
import com.ncsmap.jobposting.dto.UnmappedJobPostingProjection;
import com.ncsmap.jobposting.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobPostingRepositoryCustom {
    Page<JobPosting> search(JobPostingSearchRequest request, Pageable pageable);


    List<UnmappedJobPostingProjection> findUnmappedJobPostings();
}
