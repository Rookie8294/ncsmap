package com.ncsmap.auth.service;

import com.ncsmap.auth.oauth.*;
import com.ncsmap.auth.security.LoginUser;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.entity.Provider;
import com.ncsmap.member.entity.Role;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    /**
     * OAuth2 로그인 요청을 처리한다.
     * <p>
     * 1. OAuth2 서버(구글, 카카오, 네이버)에서 사용자 정보를 조회한다.
     * 2. Provider별 사용자 정보를 공통 객체(OAuth2UserInfo)로 변환한다.
     * 3. 기존 회원이면 조회하고, 없으면 신규 회원을 생성한다.
     * 4. Spring Security에서 사용할 LoginUser를 생성하여 반환한다.
     */

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = getRegistrationId(userRequest);
        Provider provider = getProvider(registrationId);
        OAuth2UserInfo userInfo = getUserInfo(registrationId, oAuth2User);

        log.info("소셜 로그인 요청 provider={}, providerId={}, email={}",
                provider,
                userInfo.getProviderId(),
                userInfo.getEmail()
        );

        Member member = findOrCreateMember(provider, userInfo);

        log.info("소셜 로그인 성공 memberId={}, email={}, provider={}",
                member.getId(),
                member.getEmail(),
                member.getProvider()
        );

        return createLoginUser(member, oAuth2User);
    }

    /**
     * OAuth2 로그인 요청에서 Provider 이름을 조회한다.
     * ex)
     * google
     * kakao
     * naver
     */
    private String getRegistrationId(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getRegistrationId();
    }

    /**
     * 문자열 Provider를 Provider Enum으로 변환한다.
     * 지원하지 않는 Provider인 경우 예외를 발생시킨다.
     */
    private Provider getProvider(String registrationId) {
        try {
            return Provider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("지원하지 않는 소셜 provider={}", registrationId);
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }
    }

    /**
     * Provider에 맞는 OAuth2UserInfo 구현체를 생성한다.
     * Provider마다 응답 JSON 구조가 다르므로
     * 공통 인터페이스로 변환하여 사용한다.
     */
    private OAuth2UserInfo getUserInfo(String registrationId, OAuth2User oAuth2User) {
        return OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );
    }

    /**
     * 소셜 회원을 조회한다.
     * - 기존 회원이면 기존 회원을 반환한다.
     * - 없는 경우 신규 회원을 생성한다.
     */
    private Member findOrCreateMember(Provider provider, OAuth2UserInfo userInfo) {
        return memberRepository
                .findByProviderAndProviderIdAndDeletedFalse(provider, userInfo.getProviderId())
                .map(this::loginExistingMember)
                .orElseGet(() -> createSocialMember(provider, userInfo));
    }

    /**
     * 기존 소셜 회원 로그인 처리
     * 기존 회원의 정보를 그대로 사용한다.
     */
    private Member loginExistingMember(Member member) {
        log.info("기존 소셜 회원 로그인 memberId={}, provider={}, providerId={}",
                member.getId(),
                member.getProvider(),
                member.getProviderId()
        );

        return member;
    }

    /**
     * 신규 소셜 회원을 생성한다.
     * 최초 로그인인 경우 Member를 생성하여 저장한다.
     */
    private Member createSocialMember(Provider provider, OAuth2UserInfo userInfo) {
        log.info("신규 소셜 회원 가입 처리 시작 provider={}, providerId={}, email={}",
                provider,
                userInfo.getProviderId(),
                userInfo.getEmail()
        );

        Member newMember = Member.builder()
                .email(userInfo.getEmail())
                .password(UUID.randomUUID().toString())
                .name(userInfo.getName())
                .nickname(userInfo.getNickname())
                .role(Role.USER)
                .provider(provider)
                .providerId(userInfo.getProviderId())
                .profileImg(userInfo.getProfileImage())
                .build();

        Member savedMember = memberRepository.save(newMember);

        log.info("신규 소셜 회원 가입 완료 memberId={}, email={}, provider={}",
                savedMember.getId(),
                savedMember.getEmail(),
                savedMember.getProvider()
        );

        return savedMember;
    }

    /**
     * Spring Security에서 사용할 LoginUser를 생성한다.
     */
    private OAuth2User createLoginUser(Member member, OAuth2User oAuth2User) {
        return LoginUser.fromOAuth(
                member,
                List.of(new SimpleGrantedAuthority(member.getRole().name())),
                oAuth2User.getAttributes()
        );
    }
}
