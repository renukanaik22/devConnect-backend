package com.backend.devConnectBackend.dto;

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
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
}
