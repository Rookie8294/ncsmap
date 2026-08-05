package com.ncsmap.agency.entity;

import com.ncsmap.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agency")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Agency extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agency_name", nullable = false, length = 100)
    private String agencyName;

    @Column(name = "business_number", length = 20, unique = true)
    private String businessNumber;

    @Column(name = "ceo_name", length = 50)
    private String ceoName;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "description", length = 500)
    private String description;

}
