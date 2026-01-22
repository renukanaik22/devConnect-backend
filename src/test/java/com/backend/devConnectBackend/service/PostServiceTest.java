package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.PostRequest;
import com.backend.devConnectBackend.dto.PostResponse;
import com.backend.devConnectBackend.model.Post;
import com.backend.devConnectBackend.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository);
    }

    @Test
    void createPost_Success() {
        PostRequest request = new PostRequest(
                "Test Post",
                "Test Description",
                List.of("Java", "Spring Boot"),
                true);

        Post savedPost = new Post(
                "post123",
                "Test Post",
                "Test Description",
                List.of("Java", "Spring Boot"),
                true,
                "user123",
                LocalDateTime.now(),
                LocalDateTime.now());

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        PostResponse response = postService.createPost(request, "user123");

        assertNotNull(response);
        assertEquals("post123", response.id());
        assertEquals("Test Post", response.title());
        assertEquals("Test Description", response.description());
        assertEquals(2, response.techStack().size());
        assertTrue(response.visibility());
        assertEquals("user123", response.userId());

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post capturedPost = postCaptor.getValue();
        assertEquals("Test Post", capturedPost.getTitle());
        assertEquals("Test Description", capturedPost.getDescription());
        assertEquals("user123", capturedPost.getUserId());
    }

    @Test
    void createPost_WithNullTechStack_UsesEmptyList() {
        PostRequest request = new PostRequest(
                "Test Post",
                "Test Description",
                null,
                true);

        Post savedPost = new Post(
                "post123",
                "Test Post",
                "Test Description",
                new ArrayList<>(),
                true,
                "user123",
                LocalDateTime.now(),
                LocalDateTime.now());

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        PostResponse response = postService.createPost(request, "user123");

        assertNotNull(response);
        assertTrue(response.techStack().isEmpty());
    }

    @Test
    void getAllPublicPosts_ReturnsOnlyPublicPosts() {
        List<Post> publicPostsList = List.of(
                new Post(
                        "post1",
                        "Public Post 1",
                        "Description 1",
                        List.of("Java"),
                        true,
                        "user123",
                        LocalDateTime.now(),
                        LocalDateTime.now()),
                new Post(
                        "post2",
                        "Public Post 2",
                        "Description 2",
                        List.of("Python"),
                        true,
                        "user456",
                        LocalDateTime.now(),
                        LocalDateTime.now()));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> publicPosts = new PageImpl<>(publicPostsList, pageable, publicPostsList.size());
        when(postRepository.findByVisibilityTrue(pageable)).thenReturn(publicPosts);

        Page<PostResponse> responses = postService.getAllPublicPosts(pageable);

        assertEquals(2, responses.getTotalElements());
        assertEquals("Public Post 1", responses.getContent().get(0).title());
        assertEquals("Public Post 2", responses.getContent().get(1).title());
        assertTrue(responses.getContent().get(0).visibility());
        assertTrue(responses.getContent().get(1).visibility());

        verify(postRepository).findByVisibilityTrue(pageable);
    }

    @Test
    void getAllPublicPosts_ReturnsEmptyPage_WhenNoPublicPosts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(postRepository.findByVisibilityTrue(pageable)).thenReturn(emptyPage);

        Page<PostResponse> responses = postService.getAllPublicPosts(pageable);

        assertTrue(responses.isEmpty());
        assertEquals(0, responses.getTotalElements());
        verify(postRepository).findByVisibilityTrue(pageable);
    }
}
