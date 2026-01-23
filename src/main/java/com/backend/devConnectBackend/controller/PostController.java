package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.constants.OwnerFilter;
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

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(required = false) String owner,
            @PageableDefault(size = 2, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        if (owner != null && OwnerFilter.ME.name().equalsIgnoreCase(owner)) {
            String userEmail = authentication.getName();
            Page<PostResponse> myPosts = postService.getMyPosts(userEmail, pageable);
            return ResponseEntity.ok(myPosts);
        }

        Page<PostResponse> publicPosts = postService.getAllPublicPosts(pageable);
        return ResponseEntity.ok(publicPosts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable String id,
            @Valid @RequestBody PostRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        PostResponse response = postService.updatePost(id, request, userEmail);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String id,
            Authentication authentication) {

        String userEmail = authentication.getName();
        postService.deletePost(id, userEmail);

        return ResponseEntity.noContent().build();
    }
}
