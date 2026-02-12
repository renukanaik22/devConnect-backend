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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        if (!Boolean.TRUE.equals(post.getVisibility())) {
            throw new UnauthorizedAccessException("Cannot comment on private posts");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setPost(post);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        // Atomically increment comment count
        postRepository.incrementCommentCount(postId, 1);

        return mapToResponse(savedComment);
    }

    /**
     * Get comments for a post with pagination.
     *
     * @param postId   the post ID
     * @param pageable pagination parameters (page, size, sort)
     * @return Page of CommentResponse
     */
    public Page<CommentResponse> getComments(String postId, Pageable pageable) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        if (!Boolean.TRUE.equals(post.getVisibility())) {
            throw new UnauthorizedAccessException("Cannot view comments on private posts");
        }

        Page<Comment> comments = commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);

        return comments.map(this::mapToResponse);
    }

    public void deleteComment(String commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to delete this comment");
        }

        String postId = comment.getPost().getId();
        commentRepository.delete(comment);

        // Atomically decrement comment count
        postRepository.incrementCommentCount(postId, -1);
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
