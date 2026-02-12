package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.CommentRequest;
import com.backend.devConnectBackend.dto.CommentResponse;
import com.backend.devConnectBackend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        CommentResponse response = commentService.addComment(postId, request, userEmail);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable String postId,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentResponse> comments = commentService.getComments(postId, pageable);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String commentId,
            Authentication authentication) {

        String userEmail = authentication.getName();
        commentService.deleteComment(commentId, userEmail);

        return ResponseEntity.noContent().build();
    }
}
