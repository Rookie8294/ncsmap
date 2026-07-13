package com.ncsmap.sync.controller;
import com.ncsmap.sync.dto.JobPostingSyncCondition;
import com.ncsmap.sync.service.InstitutionSyncService;
import com.ncsmap.sync.service.JobPostingSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/admin/sync")
@RequiredArgsConstructor
public class SyncController {

    private final InstitutionSyncService institutionSyncService;
    private final JobPostingSyncService jobPostingSyncService;
    
    @PostMapping("/institutions")
    public ResponseEntity<String> syncInstitutions() {
        institutionSyncService.sync();
        return ResponseEntity.ok("기관 동기화 완료");
    }

    @PostMapping("/job-postings")
    public ResponseEntity<String> syncJobPostings(
            @RequestBody(required = false) JobPostingSyncCondition condition) {

        if (condition == null) {
            condition = new JobPostingSyncCondition(
                    null,null, null, null, null);
        }
        jobPostingSyncService.sync(condition);

        return ResponseEntity.ok("채용공고 동기화 완료");
    }
}
