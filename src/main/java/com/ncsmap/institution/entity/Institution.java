package com.ncsmap.institution.entity;

import com.ncsmap.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "institution")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Institution extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("기관 코드")
    @Column(length = 30)
    private String code;

    @Comment("기관 이름")
    @Column(nullable = false, length = 100)
    private String name;

    @Comment("기관 타입")
    @Column(length = 30)
    private String type;

    @Comment("주무부처")
    @Column(length = 30)
    private String ministry;

    @Comment("도로명 주소")
    @Column(length = 200)
    private String loadAddress;

    @Comment("기관 사이트 주소")
    @Column(length = 300)
    private String siteUrl;


}
