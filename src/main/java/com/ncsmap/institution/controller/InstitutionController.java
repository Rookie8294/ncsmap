package com.ncsmap.institution.controller;

import com.ncsmap.institution.dto.InstitutionResponse;
import com.ncsmap.institution.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/institutions")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    // 기관 전체 조회
    @GetMapping
    public ResponseEntity<Page<InstitutionResponse>> getInstitutions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(institutionService.getInstitutions(page, size));
    }

    // 기관 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> getInstitution(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(institutionService.getInstitution(id));
    }

}
