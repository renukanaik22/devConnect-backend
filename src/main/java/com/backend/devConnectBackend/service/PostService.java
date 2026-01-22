package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.PostRequest;
import com.backend.devConnectBackend.dto.PostResponse;
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

        // Map Page<Post> to Page<PostResponse>
        return publicPosts.map(this::mapToResponse);
    }

    private PostResponse mapToResponse(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getTechStack(),
                post.getVisibility(),
                post.getUserId(),
                post.getCreatedAt(),
                post.getUpdatedAt());
    }
}
