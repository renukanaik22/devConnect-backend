package com.backend.devConnectBackend.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        String id,
        String content,
        String postId,
        String userId,
        String userName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
