package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        postRepository.deleteAll();
    }

    @AfterEach
    void cleanup() {
        postRepository.deleteAll();
    }

    @Test
    void savePost_Success() {
        Post post = new Post();
        post.setTitle("Test Post");
        post.setDescription("Test Description");
        post.setTechStack(List.of("Java", "Spring Boot"));
        post.setVisibility(true);
        post.setUserId("user123");

        Post savedPost = postRepository.save(post);

        assertNotNull(savedPost.getId());
        assertEquals("Test Post", savedPost.getTitle());
        assertEquals("Test Description", savedPost.getDescription());
        assertEquals(2, savedPost.getTechStack().size());
        assertTrue(savedPost.getVisibility());
        assertEquals("user123", savedPost.getUserId());
    }

    @Test
    void findByVisibilityTrue_ReturnsOnlyPublicPosts() {
        // Create public post
        Post publicPost = new Post();
        publicPost.setTitle("Public Post");
        publicPost.setDescription("Public Description");
        publicPost.setTechStack(List.of("Java"));
        publicPost.setVisibility(true);
        publicPost.setUserId("user123");
        postRepository.save(publicPost);

        // Create private post
        Post privatePost = new Post();
        privatePost.setTitle("Private Post");
        privatePost.setDescription("Private Description");
        privatePost.setTechStack(List.of("Python"));
        privatePost.setVisibility(false);
        privatePost.setUserId("user456");
        postRepository.save(privatePost);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> publicPosts = postRepository.findByVisibilityTrue(pageable);

        assertEquals(1, publicPosts.getTotalElements());
        assertEquals("Public Post", publicPosts.getContent().get(0).getTitle());
        assertTrue(publicPosts.getContent().get(0).getVisibility());
    }

    @Test
    void findByVisibilityTrue_ReturnsEmptyPage_WhenNoPublicPosts() {
        // Create only private posts
        Post privatePost = new Post();
        privatePost.setTitle("Private Post");
        privatePost.setDescription("Private Description");
        privatePost.setTechStack(List.of("Python"));
        privatePost.setVisibility(false);
        privatePost.setUserId("user456");
        postRepository.save(privatePost);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> publicPosts = postRepository.findByVisibilityTrue(pageable);

        assertTrue(publicPosts.isEmpty());
        assertEquals(0, publicPosts.getTotalElements());
    }
}
