package com.ncsmap.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Provider provider;

    @Column(name = "provider_id", length = 255)
    private String providerId;

    @Column(name = "provider_img", length = 200)
    private String profileImg;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private Member(String email,
                   String password,
                   String name,
                   String nickname,
                   Role role,
                   Provider provider,
                   String providerId,
                   String profileImg){
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImg = profileImg;
        this.deleted = false;
    }

    public void updateProfile(String nickname, String profileImg){
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

    public void withdraw(){
        this.deleted = true;
    }

    @PrePersist
    protected void onCreate(){
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        if(this.role == null){
            this.role = Role.USER;
        }

        if(this.provider == null){
            this.provider = Provider.LOCAL;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
