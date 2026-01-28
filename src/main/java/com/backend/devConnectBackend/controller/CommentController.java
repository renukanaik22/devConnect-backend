package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.CommentRequest;
import com.backend.devConnectBackend.dto.CommentResponse;
import com.backend.devConnectBackend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable String postId) {
        List<CommentResponse> comments = commentService.getComments(postId);
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
