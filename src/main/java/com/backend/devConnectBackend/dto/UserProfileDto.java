package com.backend.devConnectBackend.dto;

import java.util.List;

public record UserProfileDto(
        String name,
        String email,
        String role,
        List<String> skills) {
}
