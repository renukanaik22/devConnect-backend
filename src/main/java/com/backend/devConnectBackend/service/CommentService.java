package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.CommentRequest;
import com.backend.devConnectBackend.dto.CommentResponse;
import com.backend.devConnectBackend.exception.CommentNotFoundException;
import com.backend.devConnectBackend.exception.PostNotFoundException;
import com.backend.devConnectBackend.exception.UnauthorizedAccessException;
import com.backend.devConnectBackend.model.Comment;
import com.backend.devConnectBackend.model.Post;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.CommentRepository;
import com.backend.devConnectBackend.repository.PostRepository;
import com.backend.devConnectBackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse addComment(String postId, CommentRequest request, String userEmail) {
        // Find post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Check if post is public
        if (!Boolean.TRUE.equals(post.getVisibility())) {
            throw new UnauthorizedAccessException("Cannot comment on private posts");
        }

        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Create comment
        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setPost(post);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    public List<CommentResponse> getComments(String postId) {
        // Find post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // Check if post is public
        if (!Boolean.TRUE.equals(post.getVisibility())) {
            throw new UnauthorizedAccessException("Cannot view comments on private posts");
        }

        // Get comments
        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post);

        return comments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteComment(String commentId, String userEmail) {
        // Find comment
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        // Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Check ownership
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getUser().getEmail(),
                comment.getUser().getName(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
