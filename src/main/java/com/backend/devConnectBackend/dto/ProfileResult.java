package com.backend.devConnectBackend.dto;

import java.math.BigDecimal;
import java.util.List;

public sealed interface ProfileResult
        permits ProfileResult.FullProfile,
        ProfileResult.PublicProfile,
        ProfileResult.ProfileNotFound {

    // Used for self-profile and ADMIN viewing others - includes salary
    record FullProfile(
            String id,
            String name,
            String email,
            String role,
            List<String> skills,
            BigDecimal currentSalary,
            BigDecimal expectedSalary) implements ProfileResult {
    }

    // Used when non-ADMIN views other users - no salary
    record PublicProfile(
            String id,
            String name,
            String email,
            String role,
            List<String> skills) implements ProfileResult {
    }

    // Null Object for user not found
    record ProfileNotFound() implements ProfileResult {
    }
}
