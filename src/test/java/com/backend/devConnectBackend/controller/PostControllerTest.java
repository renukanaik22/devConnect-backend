package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.PostRequest;
import com.backend.devConnectBackend.dto.PostResponse;
import com.backend.devConnectBackend.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private PostService postService;

        @MockitoBean
        private com.backend.devConnectBackend.security.JwtService jwtService;

        @MockitoBean
        private com.backend.devConnectBackend.repository.UserRepository userRepository;

        @MockitoBean
        private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

        @Test
        @WithMockUser(username = "test@example.com")
        void createPost_Success() throws Exception {
                PostRequest request = new PostRequest(
                                "Test Post",
                                "Test Description",
                                List.of("Java", "Spring Boot"),
                                true);

                PostResponse response = new PostResponse(
                                "post123",
                                "Test Post",
                                "Test Description",
                                List.of("Java", "Spring Boot"),
                                true,
                                "test@example.com",
                                0,
                                LocalDateTime.now(),
                                LocalDateTime.now());

                when(postService.createPost(any(PostRequest.class), eq("test@example.com")))
                                .thenReturn(response);

                mockMvc.perform(post("/posts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value("post123"))
                                .andExpect(jsonPath("$.title").value("Test Post"))
                                .andExpect(jsonPath("$.description").value("Test Description"))
                                .andExpect(jsonPath("$.visibility").value(true))
                                .andExpect(jsonPath("$.userId").value("test@example.com"));
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void createPost_ValidationError_MissingTitle() throws Exception {
                PostRequest request = new PostRequest(
                                "",
                                "Test Description",
                                List.of("Java"),
                                true);

                mockMvc.perform(post("/posts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void createPost_ValidationError_MissingDescription() throws Exception {
                PostRequest request = new PostRequest(
                                "Test Post",
                                "",
                                List.of("Java"),
                                true);

                mockMvc.perform(post("/posts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void createPost_Unauthorized_WithoutAuthentication() throws Exception {
                PostRequest request = new PostRequest(
                                "Test Post",
                                "Test Description",
                                List.of("Java"),
                                true);

                mockMvc.perform(post("/posts")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void getAllPublicPosts_Success() throws Exception {
                List<PostResponse> responseList = List.of(
                                new PostResponse(
                                                "post1",
                                                "Public Post 1",
                                                "Description 1",
                                                List.of("Java"),
                                                true,
                                                "user123",
                                                0,
                                                LocalDateTime.now(),
                                                LocalDateTime.now()),
                                new PostResponse(
                                                "post2",
                                                "Public Post 2",
                                                "Description 2",
                                                List.of("Python"),
                                                true,
                                                "user456",
                                                0,
                                                LocalDateTime.now(),
                                                LocalDateTime.now()));

                Page<PostResponse> responses = new PageImpl<>(responseList, PageRequest.of(0, 10), responseList.size());
                when(postService.getAllPublicPosts(any())).thenReturn(responses);

                mockMvc.perform(get("/posts")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].title").value("Public Post 1"))
                                .andExpect(jsonPath("$.content[1].title").value("Public Post 2"))
                                .andExpect(jsonPath("$.totalElements").value(2));
        }

        @Test
        void getAllPublicPosts_Unauthorized_WithoutAuthentication() throws Exception {
                mockMvc.perform(get("/posts")
                                .with(csrf()))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void getAllPublicPosts_ReturnsEmptyPage() throws Exception {
                Page<PostResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
                when(postService.getAllPublicPosts(any())).thenReturn(emptyPage);

                mockMvc.perform(get("/posts")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(0))
                                .andExpect(jsonPath("$.totalElements").value(0));
        }
}
