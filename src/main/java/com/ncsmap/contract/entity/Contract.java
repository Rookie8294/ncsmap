package com.ncsmap.contract.entity;

import com.ncsmap.agency.entity.Agency;
import com.ncsmap.common.BaseEntity;
import com.ncsmap.institution.entity.Institution;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@Table(name = "contract")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Contract extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "institution_id")
        private Institution institution;

        @Column(name = "institution_code", nullable = false, length = 30)
        private String institutionCode;

        @Column(name = "institution_name", nullable = false, length = 200)
        private String institutionName;

        @Column(name = "institution_division", length = 30)
        private String institutionDivision;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "agency_id", nullable = false)
        private Agency agency;

//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(name = "job_posting_id")
//        private JobPosting jobPosting;

        @Column(name = "contract_number", nullable = false, unique = true, length = 30)
        private String contractNumber;

        @Column(name = "unified_contract_number", length = 30)
        private String unifiedContractNumber;

        @Column(name = "title", nullable = false, length = 500)
        private String title;

        @Column(name = "contract_method", length = 50)
        private String contractMethod;

        @Column(name = "business_type", length = 30)
        private String businessType;

        @Column(name = "contract_date")
        private LocalDate contractDate;

        @Column(name = "contract_amount")
        private Long contractAmount;

        @Column(name = "total_contract_amount")
        private Long totalContractAmount;

        @Column(name = "bid_notice_number", length = 30)
        private String bidNoticeNumber;

        @Column(name = "bid_notice_name", length = 500)
        private String bidNoticeName;

        @Column(name = "contract_info_url", length = 1000)
        private String contractInfoUrl;

//        //ncs관련 채용 용역 계약 여부
//        @Column(name = "is_ncs_exam")
//        private Boolean isNcsExam;

}
