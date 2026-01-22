package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.PostRequest;
import com.backend.devConnectBackend.dto.PostResponse;
import com.backend.devConnectBackend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName(); // Returns email from JWT
        PostResponse response = postService.createPost(request, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all public posts with pagination.
     * Default: page=0, size=20, sort by createdAt descending
     *
     * @param pageable Pagination parameters from query params
     * @return Page of PostResponse
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPublicPosts(
            @PageableDefault(size = 2, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {

        Page<PostResponse> publicPosts = postService.getAllPublicPosts(pageable);
        return ResponseEntity.ok(publicPosts);
    }
}
