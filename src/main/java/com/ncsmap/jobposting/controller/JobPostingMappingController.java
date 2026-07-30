package com.ncsmap.jobposting.controller;

import com.ncsmap.jobposting.dto.JobPostingWithContractsResponse;
import com.ncsmap.jobposting.service.JobPostingMappingService;
import com.ncsmap.jobposting.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/admin/mapping")
@RequiredArgsConstructor
public class JobPostingMappingController {

    private final JobPostingMappingService jobPostingMappingService;

    @GetMapping("/unmapped-job-postings")
    public ResponseEntity<List<JobPostingWithContractsResponse>> getUnmappedJobPostings() {
        return ResponseEntity.ok(jobPostingMappingService.getJobUnmappedJobPostings());
    }

    @PostMapping("/job-posting/{jobPostingId}/contract/{contractId}")
    public ResponseEntity<String> mapContractToJobPosting(
            @PathVariable Long jobPostingId,
            @PathVariable Long contractId
    ){
        jobPostingMappingService.mapContractToJobPosting(jobPostingId, contractId);
        return ResponseEntity.ok("매핑 완료");
    }

}
