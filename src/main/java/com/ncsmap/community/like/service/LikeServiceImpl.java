package com.ncsmap.community.like.service;

import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.common.util.SecurityUtil;
import com.ncsmap.community.like.entity.PostLike;
import com.ncsmap.community.like.repository.PostLikeRepository;
import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.PostStatus;
import com.ncsmap.community.post.repository.PostRepository;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void likePost(Long postId) {
        log.info("게시글 좋아요 처리 시작 postId={}", postId);

        Post post = getActivePost(postId);
        Member member = getCurrentMember();

        Long memberId = member.getId();

        log.debug(
                "좋아요 등록 대상 조회 완료 postId={}, memberId={}, currentLikeCount={}",
                postId,
                memberId,
                post.getLikeCount()
        );

        savePostLike(post, member);

        incrementPostLikeCount(postId, memberId);

        log.info(
                "게시글 좋아요 처리 완료 postId={}, memberId={}",
                postId,
                memberId
        );
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        log.info("게시글 좋아요 취소 처리 시작 postId={}", postId);

        Post post = getActivePost(postId);
        Member member = getCurrentMember();

        Long memberId = member.getId();

        log.debug(
                "좋아요 취소 대상 조회 완료 postId={}, memberId={}, currentLikeCount={}",
                postId,
                memberId,
                post.getLikeCount()
        );

        deletePostLike(postId, memberId);

        decrementPostLikeCount(postId, memberId);

        log.info(
                "게시글 좋아요 취소 처리 완료 postId={}, memberId={}",
                postId,
                memberId
        );
    }

    private void savePostLike(Post post, Member member) {
        Long postId = post.getId();
        Long memberId = member.getId();

        log.debug(
                "게시글 좋아요 저장 시도 postId={}, memberId={}",
                postId,
                memberId
        );

        try {
            PostLike postLike = new PostLike(post, member);
            postLikeRepository.saveAndFlush(postLike);

            log.debug(
                    "게시글 좋아요 저장 완료 postId={}, memberId={}",
                    postId,
                    memberId
            );
        } catch (DataIntegrityViolationException e) {
            log.warn(
                    "게시글 좋아요 중복 등록 감지 postId={}, memberId={}",
                    postId,
                    memberId
            );

            throw new BusinessException(ErrorCode.LIKE_ALREADY_EXISTS);
        }
    }

    private void deletePostLike(Long postId, Long memberId) {
        log.debug(
                "게시글 좋아요 삭제 시도 postId={}, memberId={}",
                postId,
                memberId
        );

        int deletedRows = postLikeRepository.deleteByPostIdAndMemberId(
                postId,
                memberId
        );

        if (deletedRows == 0) {
            log.warn(
                    "삭제할 게시글 좋아요가 존재하지 않음 postId={}, memberId={}",
                    postId,
                    memberId
            );

            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND);
        }

        log.debug(
                "게시글 좋아요 삭제 완료 postId={}, memberId={}, deletedRows={}",
                postId,
                memberId,
                deletedRows
        );
    }

    private void incrementPostLikeCount(Long postId, Long memberId) {
        log.debug(
                "게시글 좋아요 수 증가 시도 postId={}, memberId={}",
                postId,
                memberId
        );

        int updatedRows = postRepository.incrementLikeCount(
                postId,
                PostStatus.ACTIVE
        );

        if (updatedRows == 0) {
            log.warn(
                    "게시글 좋아요 수 증가 실패 - 활성 게시글 없음 postId={}, memberId={}, status={}",
                    postId,
                    memberId,
                    PostStatus.ACTIVE
            );

            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        log.debug(
                "게시글 좋아요 수 증가 완료 postId={}, memberId={}, updatedRows={}",
                postId,
                memberId,
                updatedRows
        );
    }

    private void decrementPostLikeCount(Long postId, Long memberId) {
        log.debug(
                "게시글 좋아요 수 감소 시도 postId={}, memberId={}",
                postId,
                memberId
        );

        int updatedRows = postRepository.decrementLikeCount(
                postId,
                PostStatus.ACTIVE
        );

        if (updatedRows == 0) {
            log.warn(
                    "게시글 좋아요 수 감소 실패 - 활성 게시글 없음 postId={}, memberId={}, status={}",
                    postId,
                    memberId,
                    PostStatus.ACTIVE
            );

            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        log.debug(
                "게시글 좋아요 수 감소 완료 postId={}, memberId={}, updatedRows={}",
                postId,
                memberId,
                updatedRows
        );
    }

    private Member getCurrentMember() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        log.debug("현재 로그인 회원 조회 시작 memberId={}", memberId);

        return memberRepository.findByIdAndDeletedFalse(memberId)
                .map(member -> {
                    log.debug(
                            "현재 로그인 회원 조회 완료 memberId={}",
                            member.getId()
                    );

                    return member;
                })
                .orElseThrow(() -> {
                    log.warn(
                            "현재 로그인 회원을 찾을 수 없음 memberId={}",
                            memberId
                    );

                    return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    private Post getActivePost(Long postId) {
        log.debug(
                "활성 게시글 조회 시작 postId={}, status={}",
                postId,
                PostStatus.ACTIVE
        );

        return postRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
                .map(post -> {
                    log.debug(
                            "활성 게시글 조회 완료 postId={}, status={}, likeCount={}",
                            post.getId(),
                            post.getStatus(),
                            post.getLikeCount()
                    );

                    return post;
                })
                .orElseThrow(() -> {
                    log.warn(
                            "활성 게시글을 찾을 수 없음 postId={}, status={}",
                            postId,
                            PostStatus.ACTIVE
                    );

                    return new BusinessException(ErrorCode.POST_NOT_FOUND);
                });
    }
}
