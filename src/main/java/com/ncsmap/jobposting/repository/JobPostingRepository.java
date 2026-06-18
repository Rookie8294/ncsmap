package com.ncsmap.jobposting.repository;

import com.ncsmap.jobposting.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostingRepository
        extends JpaRepository<JobPosting, Long>,
        JobPostingRepositoryCustom {
}
