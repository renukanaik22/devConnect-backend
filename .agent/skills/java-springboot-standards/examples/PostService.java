package com.example.devconnect.service;

import com.example.devconnect.dto.request.CreatePostRequest;
import com.example.devconnect.dto.request.UpdatePostRequest;
import com.example.devconnect.dto.response.PostResponse;
import com.example.devconnect.exception.ResourceNotFoundException;
import com.example.devconnect.exception.UnauthorizedException;
import com.example.devconnect.mapper.PostMapper;
import com.example.devconnect.model.Post;
import com.example.devconnect.model.User;
import com.example.devconnect.repository.PostRepository;
import com.example.devconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for post-related business logic.
 * Handles all business operations for posts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    /**
     * Creates a new post.
     *
     * @param request   the post creation request
     * @param userEmail the email of the authenticated user
     * @return the created post response
     */
    @Transactional
    public PostResponse createPost(CreatePostRequest request, String userEmail) {
        log.info("Creating post for user: {}", userEmail);

        User author = findUserByEmail(userEmail);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .tags(request.getTags())
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Post savedPost = postRepository.save(post);
        log.info("Post created successfully with ID: {}", savedPost.getId());

        return postMapper.toResponse(savedPost);
    }

    /**
     * Retrieves a post by ID.
     *
     * @param id the post ID
     * @return the post response
     * @throws ResourceNotFoundException if post not found
     */
    public PostResponse getPostById(String id) {
        log.debug("Fetching post with ID: {}", id);

        Post post = findPostById(id);
        return postMapper.toResponse(post);
    }

    /**
     * Retrieves all posts.
     *
     * @return list of all post responses
     */
    public List<PostResponse> getAllPosts() {
        log.debug("Fetching all posts");

        return postRepository.findAll().stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing post.
     *
     * @param id        the post ID
     * @param request   the update request
     * @param userEmail the email of the authenticated user
     * @return the updated post response
     * @throws ResourceNotFoundException if post not found
     * @throws UnauthorizedException     if user is not the author
     */
    @Transactional
    public PostResponse updatePost(String id, UpdatePostRequest request, String userEmail) {
        log.info("Updating post with ID: {} by user: {}", id, userEmail);

        Post post = findPostById(id);
        User user = findUserByEmail(userEmail);

        validatePostOwnership(post, user);

        updatePostFields(post, request);
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRepository.save(post);
        log.info("Post updated successfully with ID: {}", updatedPost.getId());

        return postMapper.toResponse(updatedPost);
    }

    /**
     * Deletes a post.
     *
     * @param id        the post ID
     * @param userEmail the email of the authenticated user
     * @throws ResourceNotFoundException if post not found
     * @throws UnauthorizedException     if user is not the author
     */
    @Transactional
    public void deletePost(String id, String userEmail) {
        log.info("Deleting post with ID: {} by user: {}", id, userEmail);

        Post post = findPostById(id);
        User user = findUserByEmail(userEmail);

        validatePostOwnership(post, user);

        postRepository.delete(post);
        log.info("Post deleted successfully with ID: {}", id);
    }

    /**
     * Retrieves posts by author.
     *
     * @param authorId the author's user ID
     * @return list of post responses by the author
     */
    public List<PostResponse> getPostsByAuthor(String authorId) {
        log.debug("Fetching posts by author ID: {}", authorId);

        return postRepository.findByAuthorId(authorId).stream()
                .map(postMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Private helper methods

    private Post findPostById(String id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Post not found with ID: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email));
    }

    private void validatePostOwnership(Post post, User user) {
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new UnauthorizedException(
                    "User is not authorized to modify this post");
        }
    }

    private void updatePostFields(Post post, UpdatePostRequest request) {
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getTags() != null) {
            post.setTags(request.getTags());
        }
    }
}
