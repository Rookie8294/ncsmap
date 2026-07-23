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
    public CommentResponse createComment(
            Long postId,
            CommentCreateRequest commentCreateRequest
    ) {
        Long parentCommentId = commentCreateRequest.getParentCommentId();

        log.info(
                "댓글 작성 처리 시작 postId={}, parentCommentId={}",
                postId,
                parentCommentId
        );

        Member member = getCurrentMember();
        Post post = getActivePost(postId);
        PostComment parentComment = resolveParentComment(
                postId,
                parentCommentId
        );

        PostComment comment = new PostComment(
                member,
                post,
                parentComment,
                commentCreateRequest.getContent()
        );

        PostComment savedComment = postCommentRepository.save(comment);

        log.debug(
                "댓글 저장 완료 commentId={}, postId={}, memberId={}, parentCommentId={}",
                savedComment.getId(),
                postId,
                member.getId(),
                parentCommentId
        );

        incrementPostCommentCount(postId, savedComment.getId());

        log.info(
                "댓글 작성 처리 완료 commentId={}, postId={}, memberId={}, parentCommentId={}",
                savedComment.getId(),
                postId,
                member.getId(),
                parentCommentId
        );

        return CommentResponse.from(savedComment);
    }

    @Override
    public List<CommentResponse> getComments(Long postId) {
        log.info("댓글 목록 조회 처리 시작 postId={}", postId);

        getActivePost(postId);

        List<CommentResponse> comments =
                postCommentRepository.findAllByPostIdOrderByCreatedAtAsc(postId)
                        .stream()
                        .map(CommentResponse::from)
                        .toList();

        log.info(
                "댓글 목록 조회 처리 완료 postId={}, commentCount={}",
                postId,
                comments.size()
        );

        return comments;
    }

    @Override
    @Transactional
    public CommentResponse updateComment(
            Long postId,
            Long commentId,
            CommentUpdateRequest commentUpdateRequest
    ) {
        log.info(
                "댓글 수정 처리 시작 postId={}, commentId={}",
                postId,
                commentId
        );

        getActivePost(postId);

        PostComment comment = getActiveComment(commentId);

        validateCommentPost(comment, postId);
        validateCommentOwner(comment);

        comment.update(commentUpdateRequest.getContent());

        log.info(
                "댓글 수정 처리 완료 postId={}, commentId={}, memberId={}",
                postId,
                commentId,
                comment.getMember().getId()
        );

        return CommentResponse.from(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        log.info(
                "댓글 삭제 처리 시작 postId={}, commentId={}",
                postId,
                commentId
        );

        getActivePost(postId);

        PostComment comment = getActiveComment(commentId);

        validateCommentPost(comment, postId);
        validateCommentOwner(comment);

        Long memberId = comment.getMember().getId();

        comment.delete();

        log.debug(
                "댓글 소프트 삭제 완료 postId={}, commentId={}, memberId={}",
                postId,
                commentId,
                memberId
        );

        decrementPostCommentCount(postId, commentId);

        log.info(
                "댓글 삭제 처리 완료 postId={}, commentId={}, memberId={}",
                postId,
                commentId,
                memberId
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

                    return new BusinessException(
                            ErrorCode.MEMBER_NOT_FOUND
                    );
                });
    }

    private Post getActivePost(Long postId) {
        log.debug(
                "활성 게시글 조회 시작 postId={}, status={}",
                postId,
                PostStatus.ACTIVE
        );

        return postRepository.findByIdAndStatus(
                        postId,
                        PostStatus.ACTIVE
                )
                .map(post -> {
                    log.debug(
                            "활성 게시글 조회 완료 postId={}, status={}, commentCount={}",
                            post.getId(),
                            post.getStatus(),
                            post.getCommentCount()
                    );

                    return post;
                })
                .orElseThrow(() -> {
                    log.warn(
                            "활성 게시글을 찾을 수 없음 postId={}, status={}",
                            postId,
                            PostStatus.ACTIVE
                    );

                    return new BusinessException(
                            ErrorCode.POST_NOT_FOUND
                    );
                });
    }

    private PostComment getActiveComment(Long commentId) {
        log.debug(
                "활성 댓글 조회 시작 commentId={}",
                commentId
        );

        return postCommentRepository.findByIdAndDeletedFalse(commentId)
                .map(comment -> {
                    log.debug(
                            "활성 댓글 조회 완료 commentId={}, postId={}, memberId={}",
                            comment.getId(),
                            comment.getPost().getId(),
                            comment.getMember().getId()
                    );

                    return comment;
                })
                .orElseThrow(() -> {
                    log.warn(
                            "활성 댓글을 찾을 수 없음 commentId={}",
                            commentId
                    );

                    return new BusinessException(
                            ErrorCode.COMMENT_NOT_FOUND
                    );
                });
    }

    private void validateCommentPost(
            PostComment comment,
            Long postId
    ) {
        Long actualPostId = comment.getPost().getId();

        if (!actualPostId.equals(postId)) {
            log.warn(
                    "댓글 게시글 불일치 commentId={}, requestedPostId={}, actualPostId={}",
                    comment.getId(),
                    postId,
                    actualPostId
            );

            throw new BusinessException(
                    ErrorCode.COMMENT_NOT_FOUND
            );
        }

        log.debug(
                "댓글 게시글 관계 검증 완료 commentId={}, postId={}",
                comment.getId(),
                postId
        );
    }

    private void validateCommentOwner(PostComment comment) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        Long commentOwnerId = comment.getMember().getId();

        if (!commentOwnerId.equals(currentMemberId)) {
            log.warn(
                    "댓글 접근 권한 없음 commentId={}, ownerId={}, requestMemberId={}",
                    comment.getId(),
                    commentOwnerId,
                    currentMemberId
            );

            throw new BusinessException(
                    ErrorCode.COMMENT_ACCESS_DENIED
            );
        }

        log.debug(
                "댓글 작성자 검증 완료 commentId={}, memberId={}",
                comment.getId(),
                currentMemberId
        );
    }

    private PostComment resolveParentComment(
            Long postId,
            Long parentCommentId
    ) {
        if (parentCommentId == null) {
            log.debug(
                    "일반 댓글 작성 요청 postId={}",
                    postId
            );

            return null;
        }

        log.debug(
                "부모 댓글 조회 시작 postId={}, parentCommentId={}",
                postId,
                parentCommentId
        );

        PostComment parentComment =
                postCommentRepository.findByIdAndDeletedFalse(parentCommentId)
                        .orElseThrow(() -> {
                            log.warn(
                                    "부모 댓글을 찾을 수 없음 postId={}, parentCommentId={}",
                                    postId,
                                    parentCommentId
                            );

                            return new BusinessException(
                                    ErrorCode.COMMENT_NOT_FOUND
                            );
                        });

        Long parentPostId = parentComment.getPost().getId();

        if (!parentPostId.equals(postId)) {
            log.warn(
                    "부모 댓글 게시글 불일치 parentCommentId={}, requestedPostId={}, actualPostId={}",
                    parentCommentId,
                    postId,
                    parentPostId
            );

            throw new BusinessException(
                    ErrorCode.INVALID_COMMENT_PARENT
            );
        }

        log.debug(
                "부모 댓글 검증 완료 postId={}, parentCommentId={}",
                postId,
                parentCommentId
        );

        return parentComment;
    }

    private void incrementPostCommentCount(
            Long postId,
            Long commentId
    ) {
        log.debug(
                "게시글 댓글 수 증가 시도 postId={}, commentId={}",
                postId,
                commentId
        );

        int updatedRows = postRepository.incrementCommentCount(
                postId,
                PostStatus.ACTIVE
        );

        if (updatedRows == 0) {
            log.warn(
                    "게시글 댓글 수 증가 실패 - 활성 게시글 없음 postId={}, commentId={}, status={}",
                    postId,
                    commentId,
                    PostStatus.ACTIVE
            );

            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND
            );
        }

        log.debug(
                "게시글 댓글 수 증가 완료 postId={}, commentId={}, updatedRows={}",
                postId,
                commentId,
                updatedRows
        );
    }

    private void decrementPostCommentCount(
            Long postId,
            Long commentId
    ) {
        log.debug(
                "게시글 댓글 수 감소 시도 postId={}, commentId={}",
                postId,
                commentId
        );

        int updatedRows = postRepository.decrementCommentCount(
                postId,
                PostStatus.ACTIVE
        );

        if (updatedRows == 0) {
            log.warn(
                    "게시글 댓글 수 감소 실패 - 활성 게시글 없음 postId={}, commentId={}, status={}",
                    postId,
                    commentId,
                    PostStatus.ACTIVE
            );

            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND
            );
        }

        log.debug(
                "게시글 댓글 수 감소 완료 postId={}, commentId={}, updatedRows={}",
                postId,
                commentId,
                updatedRows
        );
    }
}
