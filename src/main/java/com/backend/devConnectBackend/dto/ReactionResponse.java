package com.backend.devConnectBackend.dto;

import com.backend.devConnectBackend.constants.ReactionType;

import java.time.LocalDateTime;

public record ReactionResponse(
                String id,
                String postId,
                String userId,
                String userName,
                ReactionType type,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
