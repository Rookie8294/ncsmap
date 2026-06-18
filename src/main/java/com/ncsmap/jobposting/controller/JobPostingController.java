package com.ncsmap.jobposting.controller;

import com.ncsmap.jobposting.dto.JobPostingDetailResponse;
import com.ncsmap.jobposting.dto.JobPostingListResponse;
import com.ncsmap.jobposting.dto.JobPostingSearchRequest;
import com.ncsmap.jobposting.service.JobPostingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/job-postings")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    // 채용공고 목록 조회
    @GetMapping
    public ResponseEntity<Page<JobPostingListResponse>> search(
            @ModelAttribute JobPostingSearchRequest request,
            @PageableDefault(size = 10, page = 0, sort = "endDate", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(jobPostingService.getJobPostings(request, pageable));
    }

    // 채용공고 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<JobPostingDetailResponse> getJobPosting(@PathVariable Long id) {
        return ResponseEntity.ok(jobPostingService.getJobPosting(id));
    }

}
