package com.backend.devConnectBackend.dto;

import com.backend.devConnectBackend.constants.ReactionType;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        String id,
        String title,
        String description,
        List<String> techStack,
        Boolean visibility,
        String userId,
        Integer commentCount,
        Integer likeCount,
        Integer dislikeCount,
        ReactionType userReaction,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
