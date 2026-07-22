package com.ncsmap.community.like.service;

public interface LikeService {

    void likePost(Long postId);

    void unlikePost(Long postId);
}
