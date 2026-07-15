package com.ncsmap.community.post.dto;

import com.ncsmap.community.post.entity.BoardType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostSearchCondition {

    // 게시판 유형
    private BoardType boardType;

    // 채용공고 ID
    private Long jobPostingId;

    // 기관 ID
    private Long institutionId;

    // 검색어
    private String keyword;
}
