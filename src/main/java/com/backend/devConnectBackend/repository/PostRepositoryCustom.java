package com.backend.devConnectBackend.repository;

/**
 * Custom repository interface for Post operations that require custom MongoDB
 * queries.
 */
public interface PostRepositoryCustom {

    /**
     * Increment comment count for a post atomically using MongoDB's $inc operator.
     *
     * @param postId Post ID to increment count for
     * @param delta  Amount to increment (positive) or decrement (negative)
     */
    void incrementCommentCount(String postId, int delta);
}
