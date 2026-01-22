package com.backend.devConnectBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record PostRequest(
        @NotBlank(message = "Title is required") String title,

        @NotBlank(message = "Description is required") String description,

        List<String> techStack,

        @NotNull(message = "Visibility is required") Boolean visibility) {

    // Compact constructor to handle null techStack with default empty list
    public PostRequest {
        if (techStack == null) {
            techStack = new ArrayList<>();
        }
    }
}
