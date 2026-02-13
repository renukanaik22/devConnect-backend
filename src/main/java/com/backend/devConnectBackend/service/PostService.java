package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.constants.ReactionType;
import com.backend.devConnectBackend.dto.PostRequest;
import com.backend.devConnectBackend.dto.PostResponse;
import com.backend.devConnectBackend.exception.PostNotFoundException;
import com.backend.devConnectBackend.exception.UnauthorizedAccessException;
import com.backend.devConnectBackend.model.Post;
import com.backend.devConnectBackend.repository.PostRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ReactionService reactionService;

    public PostService(PostRepository postRepository, @Lazy ReactionService reactionService) {
        this.postRepository = postRepository;
        this.reactionService = reactionService;
    }

    public PostResponse createPost(PostRequest request, String userEmail) {
        Post post = new Post();
        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setTechStack(request.techStack());
        post.setVisibility(request.visibility());
        post.setUserId(userEmail); // Store email in userId field

        Post savedPost = postRepository.save(post);

        return mapToResponse(savedPost);
    }

    /**
     * Get all public posts with pagination.
     *
     * @param pageable Pagination parameters (page, size, sort)
     * @return Page of PostResponse
     */
    public Page<PostResponse> getAllPublicPosts(Pageable pageable) {
        Page<Post> publicPosts = postRepository.findByVisibilityTrue(pageable);

        return publicPosts.map(this::mapToResponse);
    }

    public Page<PostResponse> getMyPosts(String userEmail, Pageable pageable) {
        Page<Post> myPosts = postRepository.findByUserId(userEmail, pageable);

        return myPosts.map(this::mapToResponse);
    }

    public PostResponse updatePost(String id, PostRequest request, String userEmail) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        if (!post.getUserId().equals(userEmail)) {
            throw new UnauthorizedAccessException("You are not authorized to update this post");
        }

        post.setTitle(request.title());
        post.setDescription(request.description());
        post.setTechStack(request.techStack());
        post.setVisibility(request.visibility());

        Post updatedPost = postRepository.save(post);
        return mapToResponse(updatedPost);
    }

    public void deletePost(String id, String userEmail) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        if (!post.getUserId().equals(userEmail)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    private PostResponse mapToResponse(Post post) {
        return mapToResponse(post, null);
    }

    private PostResponse mapToResponse(Post post, String userId) {
        ReactionType userReaction = null;
        if (userId != null && reactionService != null) {
            userReaction = reactionService.getUserReaction(post.getId(), userId)
                    .orElse(null);
        }

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getTechStack(),
                post.getVisibility(),
                post.getUserId(),
                post.getCommentCount(),
                post.getLikeCount(),
                post.getDislikeCount(),
                userReaction,
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
