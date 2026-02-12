package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.CommentRequest;
import com.backend.devConnectBackend.dto.CommentResponse;
import com.backend.devConnectBackend.security.JwtAuthenticationFilter;
import com.backend.devConnectBackend.security.JwtService;
import com.backend.devConnectBackend.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private CommentService commentService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Test
        @WithMockUser(username = "test@example.com")
        void getComments_Success_WithPagination() throws Exception {
                // Given
                List<CommentResponse> commentsList = List.of(
                                new CommentResponse(
                                                "comment1",
                                                "Great post!",
                                                "post123",
                                                "user1@example.com",
                                                "User One",
                                                LocalDateTime.now(),
                                                LocalDateTime.now()),
                                new CommentResponse(
                                                "comment2",
                                                "Thanks for sharing!",
                                                "post123",
                                                "user2@example.com",
                                                "User Two",
                                                LocalDateTime.now(),
                                                LocalDateTime.now()));

                Pageable pageable = PageRequest.of(0, 2);
                Page<CommentResponse> commentsPage = new PageImpl<>(commentsList, pageable, 2);

                when(commentService.getComments(eq("post123"), any(Pageable.class)))
                                .thenReturn(commentsPage);

                // When & Then
                mockMvc.perform(get("/posts/post123/comments")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].id").value("comment1"))
                                .andExpect(jsonPath("$.content[0].content").value("Great post!"))
                                .andExpect(jsonPath("$.content[1].id").value("comment2"))
                                .andExpect(jsonPath("$.content[1].content").value("Thanks for sharing!"))
                                .andExpect(jsonPath("$.totalElements").value(2))
                                .andExpect(jsonPath("$.totalPages").value(1))
                                .andExpect(jsonPath("$.size").value(2))
                                .andExpect(jsonPath("$.number").value(0));
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void getComments_Success_WithCustomPageSize() throws Exception {
                // Given
                List<CommentResponse> commentsList = List.of(
                                new CommentResponse(
                                                "comment1",
                                                "First comment",
                                                "post123",
                                                "user1@example.com",
                                                "User One",
                                                LocalDateTime.now(),
                                                LocalDateTime.now()),
                                new CommentResponse(
                                                "comment2",
                                                "Second comment",
                                                "post123",
                                                "user2@example.com",
                                                "User Two",
                                                LocalDateTime.now(),
                                                LocalDateTime.now()),
                                new CommentResponse(
                                                "comment3",
                                                "Third comment",
                                                "post123",
                                                "user3@example.com",
                                                "User Three",
                                                LocalDateTime.now(),
                                                LocalDateTime.now()));

                Pageable pageable = PageRequest.of(0, 5);
                Page<CommentResponse> commentsPage = new PageImpl<>(commentsList, pageable, 3);

                when(commentService.getComments(eq("post123"), any(Pageable.class)))
                                .thenReturn(commentsPage);

                // When & Then
                mockMvc.perform(get("/posts/post123/comments")
                                .param("size", "5")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(3))
                                .andExpect(jsonPath("$.size").value(5))
                                .andExpect(jsonPath("$.totalElements").value(3));
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void getComments_ReturnsEmptyPage_WhenNoComments() throws Exception {
                // Given
                Pageable pageable = PageRequest.of(0, 2);
                Page<CommentResponse> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

                when(commentService.getComments(eq("post123"), any(Pageable.class)))
                                .thenReturn(emptyPage);

                // When & Then
                mockMvc.perform(get("/posts/post123/comments")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(0))
                                .andExpect(jsonPath("$.totalElements").value(0))
                                .andExpect(jsonPath("$.empty").value(true));
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void getComments_Success_WithSecondPage() throws Exception {
                // Given
                List<CommentResponse> commentsList = List.of(
                                new CommentResponse(
                                                "comment3",
                                                "Third comment",
                                                "post123",
                                                "user3@example.com",
                                                "User Three",
                                                LocalDateTime.now(),
                                                LocalDateTime.now()),
                                new CommentResponse(
                                                "comment4",
                                                "Fourth comment",
                                                "post123",
                                                "user4@example.com",
                                                "User Four",
                                                LocalDateTime.now(),
                                                LocalDateTime.now()));

                Pageable pageable = PageRequest.of(1, 2);
                Page<CommentResponse> commentsPage = new PageImpl<>(commentsList, pageable, 5);

                when(commentService.getComments(eq("post123"), any(Pageable.class)))
                                .thenReturn(commentsPage);

                // When & Then
                mockMvc.perform(get("/posts/post123/comments")
                                .param("page", "1")
                                .param("size", "2")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.number").value(1))
                                .andExpect(jsonPath("$.totalElements").value(5))
                                .andExpect(jsonPath("$.totalPages").value(3))
                                .andExpect(jsonPath("$.first").value(false))
                                .andExpect(jsonPath("$.last").value(false));
        }

        @Test
        @WithMockUser(username = "test@example.com")
        void getComments_Success_WithCustomSorting() throws Exception {
                // Given
                List<CommentResponse> commentsList = List.of(
                                new CommentResponse(
                                                "comment1",
                                                "Oldest comment",
                                                "post123",
                                                "user1@example.com",
                                                "User One",
                                                LocalDateTime.now().minusDays(2),
                                                LocalDateTime.now().minusDays(2)),
                                new CommentResponse(
                                                "comment2",
                                                "Newer comment",
                                                "post123",
                                                "user2@example.com",
                                                "User Two",
                                                LocalDateTime.now().minusDays(1),
                                                LocalDateTime.now().minusDays(1)));

                Pageable pageable = PageRequest.of(0, 2);
                Page<CommentResponse> commentsPage = new PageImpl<>(commentsList, pageable, 2);

                when(commentService.getComments(eq("post123"), any(Pageable.class)))
                                .thenReturn(commentsPage);

                // When & Then
                mockMvc.perform(get("/posts/post123/comments")
                                .param("sort", "createdAt,asc")
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].content").value("Oldest comment"))
                                .andExpect(jsonPath("$.content[1].content").value("Newer comment"));
        }

        @Test
        void getComments_Unauthorized_WithoutAuthentication() throws Exception {
                // When & Then
                mockMvc.perform(get("/posts/post123/comments"))
                                .andExpect(status().isUnauthorized());
        }
}
