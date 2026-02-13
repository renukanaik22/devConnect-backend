package com.backend.devConnectBackend.repository;

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

    /**
     * Atomically increment or decrement the like count for a post.
     *
     * @param postId Post ID
     * @param delta  Amount to increment (positive) or decrement (negative)
     */
    void incrementLikeCount(String postId, int delta);

    /**
     * Atomically increment or decrement the dislike count for a post.
     *
     * @param postId Post ID
     * @param delta  Amount to increment (positive) or decrement (negative)
     */
    void incrementDislikeCount(String postId, int delta);
}
