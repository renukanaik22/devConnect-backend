package com.backend.devConnectBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
                @NotBlank(message = "Comment content is required") @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters") String content) {
}
