package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {

    /**
     * Find all posts where visibility is true (public posts) with pagination.
     *
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of public posts
     */
    Page<Post> findByVisibilityTrue(Pageable pageable);

    /**
     * Find all posts by a specific user with pagination.
     *
     * @param userId   User's email/ID
     * @param pageable Pagination parameters
     * @return Page of user's posts
     */
    Page<Post> findByUserId(String userId, Pageable pageable);
}
