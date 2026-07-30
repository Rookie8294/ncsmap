package com.ncsmap.member.service;

import com.ncsmap.auth.security.LoginUser;
import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.common.file.FileStorageService;
import com.ncsmap.common.file.LocalFileStorageService;
import com.ncsmap.common.util.SecurityUtil;
import com.ncsmap.member.dto.MemberDetailResponse;
import com.ncsmap.member.dto.MemberResponse;
import com.ncsmap.member.dto.MemberUpdateRequest;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.entity.Provider;
import com.ncsmap.member.entity.Role;
import com.ncsmap.member.dto.SignUpRequest;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final LocalFileStorageService localFileStorageService;

    @Override
    public MemberResponse signUp(SignUpRequest request) {

        validateDuplicateEmail(request.getEmail());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .role(Role.USER)
                .provider(Provider.LOCAL)
                .build();

        memberRepository.save(member);

        return new MemberResponse(member);
    }

    @Override
    public void validateDuplicateEmail(String email) {

        if (memberRepository.existsByEmailAndDeletedFalse(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Override
    public MemberDetailResponse getMyInfo(Long memberId){
        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> {
                    log.warn("로그인 사용자 조회 실패 memberId={}", memberId);
                    return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
                });

        log.info("로그인 사용자 조회 성공 memberId={}", member.getId());

        return new MemberDetailResponse(member);
    }

    @Override
    public MemberDetailResponse updateMyInfo(MemberUpdateRequest request, MultipartFile file) {
        Member member = getCurrentMember();

        log.info("요청 정보 name={}, nickname={}", request.getName(), request.getNickname());

        validateUpdateRequest(request);
        validateDuplicateNickname(member, request.getNickname());

        String profileImg = uploadProfileImageIfExists(member.getId(),file);

        member.updateProfile(
                request.getName(),
                request.getNickname(),
                profileImg
        );

        log.info("회원정보 수정 성공 memberId={}", member.getId());

        return new MemberDetailResponse(member);
    }

    @Override
    public void withdraw(Long memberId){
        log.info("회원 탈퇴 요청 memberId={}", memberId);

        Member member = memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        member.withdraw();

        log.info("회원 탈퇴 성공 memberId={}", memberId);
    }

    private Member getCurrentMember(){
        Long memberId = SecurityUtil.getCurrentMemberId();

        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn(
                            "현재 회원 조회 실패 reason=회원 정보 없음, memberId={}",
                            memberId
                    );

                    return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    private void validateUpdateRequest(MemberUpdateRequest request) {

        log.debug("회원정보 수정 요청값 검증 시작");

        if (request.getName() != null && request.getName().isBlank()) {

            log.warn("회원정보 수정 실패 reason=이름 공백");

            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        if (request.getNickname() != null && request.getNickname().isBlank()) {

            log.warn("회원정보 수정 실패 reason=닉네임 공백");

            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        log.debug("회원정보 수정 요청값 검증 완료");
    }

    private void validateDuplicateNickname(Member member, String nickname) {

        if (nickname == null) {
            log.debug("닉네임 변경 없음 memberId={}", member.getId());
            return;
        }

        if (nickname.equals(member.getNickname())) {
            log.debug("닉네임 동일 memberId={}, nickname={}",
                    member.getId(),
                    nickname);
            return;
        }

        log.info(
                "닉네임 중복 검사 시작 memberId={}, currentNickname={}, requestNickname={}",
                member.getId(),
                member.getNickname(),
                nickname
        );

        if (memberRepository.existsByNickname(nickname)) {

            log.warn(
                    "닉네임 중복 검사 실패 memberId={}, duplicateNickname={}",
                    member.getId(),
                    nickname
            );

            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        log.info(
                "닉네임 중복 검사 완료 memberId={}, nickname={}",
                member.getId(),
                nickname
        );
    }

    private String uploadProfileImageIfExists(Long memberId, MultipartFile file) {

        if (file == null || file.isEmpty()) {

            log.debug("프로필 이미지 변경 없음 memberId={}", memberId);

            return null;
        }

        log.info(
                "프로필 이미지 업로드 시작 memberId={}, originalFilename={}, contentType={}, size={}bytes",
                memberId,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
        );

        String imageUrl = localFileStorageService.storageProfileImage(file);

        log.info(
                "프로필 이미지 업로드 완료 memberId={}, imageUrl={}",
                memberId,
                imageUrl
        );

        return imageUrl;
    }
}
