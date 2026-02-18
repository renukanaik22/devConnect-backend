package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for atomic Post operations.
 */
public interface PostRepositoryCustom {

    /**
     * Atomically increment or decrement the comment count for a post.
     *
     * @param postId Post ID
     * @param delta  Amount to increment (positive) or decrement (negative)
     */
    void incrementCommentCount(String postId, int delta);


    void incrementLikeCount(String postId, int delta);

    void incrementDislikeCount(String postId, int delta);

    Page<Post> searchPublicPosts(String techStack, String title, Pageable pageable);
}
