package com.ncsmap.institution.entity;

import com.ncsmap.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "institution")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    private String roadAddress;

    @Comment("기관 사이트 주소")
    @Column(length = 300)
    private String siteUrl;

    @Comment("나라장터 수요기관 코드")
    @Column(length = 100)
    private String naraCode;


    public void update(
            String name, String type, String ministry,
            String roadAddress, String siteUrl
    ) {
        this.name = name;
        this.type = type;
        this.ministry = ministry;
        this.roadAddress = roadAddress;
        this.siteUrl = siteUrl;
    }

    //나라장터 수요기관 코드 업데이트
    public void updateNaraCode(String naraCode) {
        this.naraCode = naraCode;
    }

}
