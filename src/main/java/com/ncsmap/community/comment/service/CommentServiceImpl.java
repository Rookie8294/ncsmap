package com.ncsmap.community.comment.service;

import com.ncsmap.community.comment.dto.CommentCreateRequest;
import com.ncsmap.community.comment.dto.CommentResponse;
import com.ncsmap.community.comment.dto.CommentUpdateRequest;
import com.ncsmap.community.comment.entity.PostComment;
import com.ncsmap.community.comment.repository.PostCommentRepository;
import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.common.util.SecurityUtil;
import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.PostStatus;
import com.ncsmap.community.post.repository.PostRepository;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;

    /**
     * 특정 게시글에 댓글 또는 대댓글을 작성합니다.
     *
     * parentCommentId가 null이면 일반 댓글을 작성하고,
     * 값이 있으면 해당 댓글의 대댓글을 작성합니다.
     */
    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest commentCreateRequest) {
        Member member = getCurrentMember();
        Post post = getActivePost(postId);
        PostComment parent = resolveParentComment(postId, commentCreateRequest.getParentCommentId());

        PostComment comment = new PostComment(
                member, post, parent, commentCreateRequest.getContent()
        );
        PostComment savedComment = postCommentRepository.save(comment);
        int updatedRows = postRepository.incrementCommentCount(postId, PostStatus.ACTIVE);
        if (updatedRows == 0) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);
        }

        return CommentResponse.from(savedComment);
    }

    @Override
    public List<CommentResponse> getComments(Long postId) {
        getActivePost(postId);
        return postCommentRepository.findAllByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse updateComment(
            Long postId,
            Long commentId,
            CommentUpdateRequest commentUpdateRequest
    ) {
        getActivePost(postId);
        PostComment comment = getActiveComment(commentId);
        validateCommentPost(comment, postId);
        validateCommentOwner(comment);

        comment.update(commentUpdateRequest.getContent());
        return CommentResponse.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        getActivePost(postId);
        PostComment comment = getActiveComment(commentId);
        validateCommentPost(comment, postId);
        validateCommentOwner(comment);

        comment.delete();
        int updatedRows = postRepository.decrementCommentCount(postId, PostStatus.ACTIVE);
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

    private PostComment getActiveComment(Long commentId) {
        return postCommentRepository.findByIdAndDeletedFalse(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private void validateCommentPost(PostComment comment, Long postId) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
        }
    }

    private void validateCommentOwner(PostComment comment) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        if (!comment.getMember().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.COMMENT_ACCESS_DENIED);
        }
    }

    private PostComment resolveParentComment(Long postId, Long parentCommentId) {
        if (parentCommentId == null) {
            return null;
        }

        PostComment parent = postCommentRepository.findByIdAndDeletedFalse(parentCommentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        if (!parent.getPost().getId().equals(postId)) {
            throw new BusinessException(ErrorCode.INVALID_COMMENT_PARENT);
        }

        return parent;
    }
}
