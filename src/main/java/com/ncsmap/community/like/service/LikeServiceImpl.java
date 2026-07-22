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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeServiceImpl implements LikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void likePost(Long postId) {
        Post post = getActivePost(postId);
        Member member = getCurrentMember();

        try {
            postLikeRepository.saveAndFlush(new PostLike(post, member));
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.LIKE_ALREADY_EXISTS);
        }

        int updatedRows = postRepository.incrementLikeCount(postId, PostStatus.ACTIVE);
        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void unlikePost(Long postId) {
        getActivePost(postId);
        Long memberId = getCurrentMember().getId();

        int deletedRows = postLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        if (deletedRows == 0) {
            throw new BusinessException(ErrorCode.LIKE_NOT_FOUND);
        }

        int updatedRows = postRepository.decrementLikeCount(postId, PostStatus.ACTIVE);
        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }
    }

    private Member getCurrentMember() {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return memberRepository.findByIdAndDeletedFalse(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Post getActivePost(Long postId) {
        return postRepository.findByIdAndStatus(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }
}
