package com.example.devconnect.controller;

import com.example.devconnect.dto.request.CreatePostRequest;
import com.example.devconnect.dto.request.UpdatePostRequest;
import com.example.devconnect.dto.response.PostResponse;
import com.example.devconnect.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing posts.
 * Handles all HTTP requests related to post operations.
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;

    /**
     * Creates a new post.
     *
     * @param request     the post creation request
     * @param userDetails the authenticated user
     * @return the created post response with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.createPost(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a post by ID.
     *
     * @param id the post ID
     * @return the post response with HTTP 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String id) {
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all posts.
     *
     * @return list of all posts with HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> responses = postService.getAllPosts();
        return ResponseEntity.ok(responses);
    }

    /**
     * Updates an existing post.
     *
     * @param id          the post ID
     * @param request     the update request
     * @param userDetails the authenticated user
     * @return the updated post response with HTTP 200 status
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable String id,
            @Valid @RequestBody UpdatePostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        PostResponse response = postService.updatePost(id, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a post.
     *
     * @param id          the post ID
     * @param userDetails the authenticated user
     * @return HTTP 204 No Content status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves posts by author.
     *
     * @param authorId the author's user ID
     * @return list of posts by the author with HTTP 200 status
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostResponse>> getPostsByAuthor(@PathVariable String authorId) {
        List<PostResponse> responses = postService.getPostsByAuthor(authorId);
        return ResponseEntity.ok(responses);
    }
}
