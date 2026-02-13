package com.backend.devConnectBackend.dto;

import com.backend.devConnectBackend.constants.ReactionType;
import jakarta.validation.constraints.NotNull;

public record ReactionRequest(
        @NotNull(message = "Reaction type is required") ReactionType type) {
}
