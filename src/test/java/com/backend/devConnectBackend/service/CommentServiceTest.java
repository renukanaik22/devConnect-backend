package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.CommentResponse;
import com.backend.devConnectBackend.exception.PostNotFoundException;
import com.backend.devConnectBackend.exception.UnauthorizedAccessException;
import com.backend.devConnectBackend.model.Comment;
import com.backend.devConnectBackend.model.Post;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.CommentRepository;
import com.backend.devConnectBackend.repository.PostRepository;
import com.backend.devConnectBackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private CommentService commentService;

    private Post publicPost;
    private Post privatePost;
    private User user;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postRepository, userRepository);

        user = new User();
        user.setId("user123");
        user.setEmail("test@example.com");
        user.setName("Test User");

        publicPost = new Post();
        publicPost.setId("post123");
        publicPost.setTitle("Public Post");
        publicPost.setVisibility(true);

        privatePost = new Post();
        privatePost.setId("post456");
        privatePost.setTitle("Private Post");
        privatePost.setVisibility(false);
    }

    @Test
    void getComments_WithPagination_ReturnsPagedResults() {
        // Given
        List<Comment> commentsList = List.of(
                createComment("comment1", "Great post!", publicPost, user),
                createComment("comment2", "Thanks for sharing!", publicPost, user));

        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> commentsPage = new PageImpl<>(commentsList, pageable, commentsList.size());

        when(postRepository.findById("post123")).thenReturn(Optional.of(publicPost));
        when(commentRepository.findByPostOrderByCreatedAtDesc(publicPost, pageable))
                .thenReturn(commentsPage);

        // When
        Page<CommentResponse> result = commentService.getComments("post123", pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("comment1", result.getContent().get(0).id());
        assertEquals("Great post!", result.getContent().get(0).content());
        assertEquals("comment2", result.getContent().get(1).id());
        assertEquals("Thanks for sharing!", result.getContent().get(1).content());

        verify(postRepository).findById("post123");
        verify(commentRepository).findByPostOrderByCreatedAtDesc(publicPost, pageable);
    }

    @Test
    void getComments_ReturnsEmptyPage_WhenNoComments() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        Page<Comment> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(postRepository.findById("post123")).thenReturn(Optional.of(publicPost));
        when(commentRepository.findByPostOrderByCreatedAtDesc(publicPost, pageable))
                .thenReturn(emptyPage);

        // When
        Page<CommentResponse> result = commentService.getComments("post123", pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        verify(postRepository).findById("post123");
        verify(commentRepository).findByPostOrderByCreatedAtDesc(publicPost, pageable);
    }

    @Test
    void getComments_ThrowsException_WhenPostNotFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        when(postRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(PostNotFoundException.class,
                () -> commentService.getComments("nonexistent", pageable));

        verify(postRepository).findById("nonexistent");
        verify(commentRepository, never()).findByPostOrderByCreatedAtDesc(any(), any());
    }

    @Test
    void getComments_ThrowsException_WhenPostIsPrivate() {
        // Given
        Pageable pageable = PageRequest.of(0, 2);
        when(postRepository.findById("post456")).thenReturn(Optional.of(privatePost));

        // When & Then
        assertThrows(UnauthorizedAccessException.class,
                () -> commentService.getComments("post456", pageable));

        verify(postRepository).findById("post456");
        verify(commentRepository, never()).findByPostOrderByCreatedAtDesc(any(), any());
    }

    @Test
    void getComments_WithCustomPageSize_ReturnsCorrectPageSize() {
        // Given
        List<Comment> commentsList = List.of(
                createComment("comment1", "First comment", publicPost, user),
                createComment("comment2", "Second comment", publicPost, user),
                createComment("comment3", "Third comment", publicPost, user));

        Pageable pageable = PageRequest.of(0, 5);
        Page<Comment> commentsPage = new PageImpl<>(commentsList, pageable, 3);

        when(postRepository.findById("post123")).thenReturn(Optional.of(publicPost));
        when(commentRepository.findByPostOrderByCreatedAtDesc(publicPost, pageable))
                .thenReturn(commentsPage);

        // When
        Page<CommentResponse> result = commentService.getComments("post123", pageable);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertEquals(3, result.getContent().size());
        assertEquals(5, result.getSize()); // Page size is 5
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(commentRepository).findByPostOrderByCreatedAtDesc(publicPost, pageable);
    }

    @Test
    void getComments_SecondPage_ReturnsCorrectPage() {
        // Given
        List<Comment> commentsList = List.of(
                createComment("comment3", "Third comment", publicPost, user),
                createComment("comment4", "Fourth comment", publicPost, user));

        Pageable pageable = PageRequest.of(1, 2); // Second page, size 2
        Page<Comment> commentsPage = new PageImpl<>(commentsList, pageable, 5); // Total 5 comments

        when(postRepository.findById("post123")).thenReturn(Optional.of(publicPost));
        when(commentRepository.findByPostOrderByCreatedAtDesc(publicPost, pageable))
                .thenReturn(commentsPage);

        // When
        Page<CommentResponse> result = commentService.getComments("post123", pageable);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(1, result.getNumber()); // Page number is 1 (second page)
        assertFalse(result.isFirst());
        assertFalse(result.isLast());

        verify(commentRepository).findByPostOrderByCreatedAtDesc(publicPost, pageable);
    }

    // Helper method to create Comment objects
    private Comment createComment(String id, String content, Post post, User user) {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return comment;
    }
}
