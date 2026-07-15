package com.ncsmap.community.post.service;

import com.ncsmap.common.exception.BusinessException;
import com.ncsmap.common.exception.ErrorCode;
import com.ncsmap.common.util.SecurityUtil;
import com.ncsmap.community.post.dto.*;
import com.ncsmap.community.post.entity.BoardType;
import com.ncsmap.community.post.entity.Post;
import com.ncsmap.community.post.entity.PostStatus;
import com.ncsmap.community.post.repository.PostRepository;
import com.ncsmap.institution.entity.Institution;
import com.ncsmap.institution.repository.InstitutionRepository;
import com.ncsmap.jobposting.entity.JobPosting;
import com.ncsmap.jobposting.repository.JobPostingRepository;
import com.ncsmap.member.entity.Member;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final InstitutionRepository institutionRepository;
    private final JobPostingRepository jobPostingRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시글을 생성한다.
     */
    @Override
    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        log.info(
                "게시글 생성 요청 boardType={}, title={}",
                request.getBoardType(),
                request.getTitle()
        );

        Member member = getCurrentMember();

        PostRelation relation = resolvePostRelation(
                request.getBoardType(),
                request.getJobPostingId(),
                request.getInstitutionId()
        );

        Post post = Post.createPost(
                member,
                relation.jobPosting(),
                relation.institution(),
                request.getBoardType(),
                request.getTitle(),
                request.getContent()
        );

        Post savedPost = postRepository.save(post);

        log.info(
                "게시글 생성 성공 postId={}, memberId={}, boardType={}, jobPostingId={}, institutionId={}",
                savedPost.getId(),
                member.getId(),
                savedPost.getBoardType(),
                relation.jobPosting() != null ? relation.jobPosting().getId() : null,
                relation.institution() != null ? relation.institution().getId() : null
        );

        return PostResponse.from(savedPost);
    }

    // 전체 또는 조건을 이용한 게시물 조회
    @Override
    public Page<PostListResponse> getPostList(
            PostSearchCondition condition,
            Pageable pageable
    ) {
        log.info(
                "게시글 목록 조회 요청 boardType={}, jobPostingId={}, institutionId={}, keyword={}, page={}, size={}",
                condition.getBoardType(),
                condition.getJobPostingId(),
                condition.getInstitutionId(),
                condition.getKeyword(),
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Post> posts = postRepository.searchPost(condition, pageable);

        log.info(
                "게시글 목록 조회 성공 currentCount={}, totalCount={}, totalPages={}",
                posts.getNumberOfElements(),
                posts.getTotalElements(),
                posts.getTotalPages()
        );

        return posts.map(PostListResponse::from);
    }

    /**
     * 게시글 상세를 조회한다.
     */
    @Override
    @Transactional
    public PostResponse getPost(Long postId) {
        log.info("게시글 상세 조회 요청 postId={}", postId);

        Post post = getActivePost(postId);

        post.increaseViewCount();

        log.info(
                "게시글 상세 조회 성공 postId={}, viewCount={}",
                post.getId(),
                post.getViewCount()
        );

        return PostResponse.from(post);
    }

    /**
     * 게시글을 수정한다.
     */
    @Override
    @Transactional
    public PostResponse updatePost(
            Long postId,
            PostUpdateRequest request
    ) {
        log.info(
                "게시글 수정 요청 postId={}, boardType={}",
                postId,
                request.getBoardType()
        );

        Post post = getActivePost(postId);
        validatePostOwner(post);

        PostRelation relation = resolvePostRelation(
                request.getBoardType(),
                request.getJobPostingId(),
                request.getInstitutionId()
        );

        post.update(
                request.getBoardType(),
                relation.jobPosting(),
                relation.institution(),
                request.getTitle(),
                request.getContent()
        );

        log.info(
                "게시글 수정 성공 postId={}, memberId={}",
                post.getId(),
                post.getMember().getId()
        );

        return PostResponse.from(post);
    }

    /**
     * 게시글을 삭제 상태로 변경한다.
     */
    @Override
    @Transactional
    public void deletePost(Long postId) {
        log.info("게시글 삭제 요청 postId={}", postId);

        Post post = getActivePost(postId);
        validatePostOwner(post);

        post.delete();

        log.info(
                "게시글 삭제 성공 postId={}, memberId={}",
                post.getId(),
                post.getMember().getId()
        );
    }

    /**
     * 현재 로그인한 회원을 조회한다.
     */
    private Member getCurrentMember() {
        Long memberId = SecurityUtil.getCurrentMemberId();

        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn(
                            "현재 회원 조회 실패 reason=회원 없음, memberId={}",
                            memberId
                    );

                    return new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
                });
    }

    /**
     * 삭제되지 않은 게시글을 조회한다.
     */
    private Post getActivePost(Long postId) {
        return postRepository.findByIdAndStatus(
                        postId,
                        PostStatus.ACTIVE
                )
                .orElseThrow(() -> {
                    log.warn(
                            "게시글 조회 실패 reason=게시글 없음, postId={}",
                            postId
                    );

                    return new BusinessException(ErrorCode.POST_NOT_FOUND);
                });
    }

    /**
     * 현재 로그인한 회원이 게시글 작성자인지 검증한다.
     */
    private void validatePostOwner(Post post) {
        Long currentMemberId = SecurityUtil.getCurrentMemberId();
        Long writerId = post.getMember().getId();

        if (!writerId.equals(currentMemberId)) {
            log.warn(
                    "게시글 권한 검증 실패 postId={}, writerId={}, currentMemberId={}",
                    post.getId(),
                    writerId,
                    currentMemberId
            );

            throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
        }
    }

    /**
     * 게시판 유형에 따라 게시글과 연결할 채용공고 또는 기관을 결정한다.
     */
    private PostRelation resolvePostRelation(
            BoardType boardType,
            Long jobPostingId,
            Long institutionId
    ) {
        if (boardType == null) {
            log.warn("게시글 연관관계 결정 실패 reason=게시판 유형 없음");
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        return switch (boardType) {
            case FREE, STUDY -> resolveGeneralBoardRelation(
                    boardType,
                    jobPostingId,
                    institutionId
            );

            case JOB_POSTING -> resolveJobPostingRelation(
                    jobPostingId,
                    institutionId
            );

            case INSTITUTION -> resolveInstitutionRelation(
                    jobPostingId,
                    institutionId
            );
        };
    }

    /**
     * 자유게시판과 스터디 게시판의 연관관계를 결정한다.
     */
    private PostRelation resolveGeneralBoardRelation(
            BoardType boardType,
            Long jobPostingId,
            Long institutionId
    ) {
        if (jobPostingId != null || institutionId != null) {
            log.warn(
                    "일반 게시판 연관관계 검증 실패 boardType={}, jobPostingId={}, institutionId={}",
                    boardType,
                    jobPostingId,
                    institutionId
            );

            throw new BusinessException(ErrorCode.INVALID_POST_RELATION);
        }

        return PostRelation.empty();
    }

    /**
     * 채용공고 게시판의 연관관계를 결정한다.
     */
    private PostRelation resolveJobPostingRelation(
            Long jobPostingId,
            Long institutionId
    ) {
        if (jobPostingId == null || institutionId != null) {
            log.warn(
                    "채용공고 게시판 연관관계 검증 실패 jobPostingId={}, institutionId={}",
                    jobPostingId,
                    institutionId
            );

            throw new BusinessException(ErrorCode.INVALID_POST_RELATION);
        }

        JobPosting jobPosting = getJobPosting(jobPostingId);

        return PostRelation.withJobPosting(jobPosting);
    }

    /**
     * 기관 게시판의 연관관계를 결정한다.
     */
    private PostRelation resolveInstitutionRelation(
            Long jobPostingId,
            Long institutionId
    ) {
        if (institutionId == null || jobPostingId != null) {
            log.warn(
                    "기관 게시판 연관관계 검증 실패 jobPostingId={}, institutionId={}",
                    jobPostingId,
                    institutionId
            );

            throw new BusinessException(ErrorCode.INVALID_POST_RELATION);
        }

        Institution institution = getInstitution(institutionId);

        return PostRelation.withInstitution(institution);
    }

    /**
     * 채용공고를 ID로 조회한다.
     */
    private JobPosting getJobPosting(Long jobPostingId) {
        return jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> {
                    log.warn(
                            "채용공고 조회 실패 reason=채용공고 없음, jobPostingId={}",
                            jobPostingId
                    );

                    return new BusinessException(
                            ErrorCode.JOB_POSTING_NOT_FOUND
                    );
                });
    }

    /**
     * 기관을 ID로 조회한다.
     */
    private Institution getInstitution(Long institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(() -> {
                    log.warn(
                            "기관 조회 실패 reason=기관 없음, institutionId={}",
                            institutionId
                    );

                    return new BusinessException(
                            ErrorCode.INSTITUTION_NOT_FOUND
                    );
                });
    }

    /**
     * 게시글에 연결할 채용공고와 기관 정보를 함께 관리한다.
     */
    private record PostRelation(
            JobPosting jobPosting,
            Institution institution
    ) {

        private static PostRelation empty() {
            return new PostRelation(null, null);
        }

        private static PostRelation withJobPosting(
                JobPosting jobPosting
        ) {
            return new PostRelation(jobPosting, null);
        }

        private static PostRelation withInstitution(
                Institution institution
        ) {
            return new PostRelation(null, institution);
        }
    }
}
