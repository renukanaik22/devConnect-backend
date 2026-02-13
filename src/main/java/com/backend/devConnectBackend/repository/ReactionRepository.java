package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.constants.ReactionType;
import com.backend.devConnectBackend.model.Reaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Repository interface for Reaction entity.
 * Handles database operations for post reactions (likes/dislikes).
 */
public interface ReactionRepository extends MongoRepository<Reaction, String> {

    /**
     * Find a reaction by post ID and user ID.
     * Used to check if a user has already reacted to a post.
     *
     * @param postId Post ID
     * @param userId User ID
     * @return Optional containing the reaction if found
     */
    Optional<Reaction> findByPostIdAndUserId(String postId, String userId);

    /**
     * Find all reactions for a post with pagination.
     *
     * @param postId   Post ID
     * @param pageable Pagination parameters
     * @return Page of reactions
     */
    Page<Reaction> findByPostId(String postId, Pageable pageable);

    /**
     * Find reactions for a post filtered by type with pagination.
     *
     * @param postId   Post ID
     * @param type     Reaction type (LIKE or DISLIKE)
     * @param pageable Pagination parameters
     * @return Page of reactions
     */
    Page<Reaction> findByPostIdAndType(String postId, ReactionType type, Pageable pageable);

    /**
     * Delete a reaction by post ID and user ID.
     *
     * @param postId Post ID
     * @param userId User ID
     */
    void deleteByPostIdAndUserId(String postId, String userId);
}
