package com.ncsmap.jobposting.entity;

import com.ncsmap.common.BaseEntity;
import com.ncsmap.institution.entity.Institution;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.time.LocalDate;

@Entity
@Table(name = "job_posting")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPosting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution  institution;

    @Comment("공고 고유번호")
    @Column(nullable = false, length = 100)
    private String sourcePostingId;

    @Comment("공고 제목")
    @Column(nullable = false, length = 300)
    private String title;

    @Comment("고용형태")
    @Column(length = 30)
    private String hireType;

    @Comment("채용구분")
    @Column(length = 30)
    private String recruitType;

    @Comment("학력조건")
    @Column(length = 50)
    private String eduReq;

    @Comment("근무지역")
    @Column(length = 50)
    private String workRegion;

    @Comment("채용인원")
    private Integer recruitCount;

    @Comment("공고 시작일")
    private LocalDate startDate;

    @Comment("공고 종료일")
    private LocalDate endDate;

    @Comment("NCS시험 여부")
    @Column(nullable = false)
    private Boolean ncsYn;

    @Comment("NCS 코드")
    @Column(length = 200)
    private String ncsCodes;

    @Comment("NCS 코드 이름")
    @Column(length = 200)
    private String ncsCodeNames;

    @Comment("진행상태")
    @Column(nullable = false, length = 20)
    private String status;

    @Comment("채용공고 URL")
    @Column(length = 500)
    private String sourceUrl;

    @Comment("채용공고 출처")
    @Column(nullable = false, length = 20)
    private String sourceType;

    @Comment("지원자격")
    @Column(columnDefinition = "TEXT")
    private String applyQualification;

    @Comment("결격사유")
    @Column(columnDefinition = "TEXT")
    private String disqualifyReason;

    @Comment("전형절차")
    @Column(columnDefinition = "TEXT")
    private String processDesc;

    @Comment("우대사항")
    @Column(columnDefinition = "TEXT")
    private String preferential;

    @Comment("우대사항 요약")
    @Column(length = 500)
    private String preferCondition;

    public void update(String title, String hireType, String recruitType,
                       String eduReq, String workRegion, Integer recruitCount,
                       LocalDate startDate, LocalDate endDate,
                       Boolean ncsYn, String ncsCodes, String ncsCodeNames,
                       String status, String sourceUrl,
                       String applyQualification, String disqualifyReason,
                       String processDesc, String preferential, String preferCondition) {
        this.title = title;
        this.hireType = hireType;
        this.recruitType = recruitType;
        this.eduReq = eduReq;
        this.workRegion = workRegion;
        this.recruitCount = recruitCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ncsYn = ncsYn;
        this.ncsCodes = ncsCodes;
        this.ncsCodeNames = ncsCodeNames;
        this.status = status;
        this.sourceUrl = sourceUrl;
        this.applyQualification = applyQualification;
        this.disqualifyReason = disqualifyReason;
        this.processDesc = processDesc;
        this.preferential = preferential;
        this.preferCondition = preferCondition;
    }

}
