package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.PostRequest;
import com.backend.devConnectBackend.dto.PostResponse;
import com.backend.devConnectBackend.exception.PostNotFoundException;
import com.backend.devConnectBackend.exception.UnauthorizedAccessException;
import com.backend.devConnectBackend.model.Post;
import com.backend.devConnectBackend.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
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
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getTechStack(),
                post.getVisibility(),
                post.getUserId(),
                post.getCommentCount(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
