package com.ncsmap.community.comment.service;

import com.ncsmap.community.comment.dto.CommentCreateRequest;
import com.ncsmap.community.comment.dto.CommentResponse;
import com.ncsmap.community.comment.repository.PostCommentRepository;
import com.ncsmap.community.post.repository.PostRepository;
import com.ncsmap.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private MemberRepository memberRepository;
    private PostRepository postRepository;
    private PostCommentRepository postCommentRepository;

    /**
     * 특정 게시글에 댓글 또는 대댓글을 작성합니다.
     *
     * parentCommentId가 null이면 일반 댓글을 작성하고,
     * 값이 있으면 해당 댓글의 대댓글을 작성합니다.
     */
    @Override
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest commentCreateRequest) {



        return null;
    }
}
