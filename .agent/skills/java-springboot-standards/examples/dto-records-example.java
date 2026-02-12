package com.backend.devConnectBackend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Example DTOs using Java Records - Modern, Immutable, Concise
 * 
 * Records are the PREFERRED way to define DTOs in modern Java applications.
 * They provide immutability, reduce boilerplate, and clearly express intent.
 */

// ============================================================================
// REQUEST DTOs
// ============================================================================

/**
 * Request DTO for creating a new post.
 * Uses compact constructor to provide default empty list for techStack.
 */
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

/**
 * Request DTO for updating an existing post.
 * All fields are optional (nullable) for partial updates.
 */
public record UpdatePostRequest(
        String title,
        String description,
        List<String> techStack,
        Boolean visibility) {

    // Compact constructor for null-safe collections
    public UpdatePostRequest {
        if (techStack == null) {
            techStack = new ArrayList<>();
        }
    }
}

/**
 * Request DTO for user registration.
 * Demonstrates comprehensive validation annotations.
 */
public record RegisterRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,

        @NotBlank(message = "Password is required") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String password,

        @NotBlank(message = "Name is required") @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters") String name,

        @NotNull(message = "Role is required") Role role,

        List<String> skills) {

    // Compact constructor for default values
    public RegisterRequest {
        if (skills == null) {
            skills = new ArrayList<>();
        }
    }
}

/**
 * Request DTO for user login.
 * Simple record with validation.
 */
public record LoginRequest(
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,

        @NotBlank(message = "Password is required") String password) {
}

/**
 * Request DTO for creating a comment.
 */
public record CreateCommentRequest(
        @NotBlank(message = "Content is required") @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters") String content,

        @NotBlank(message = "Post ID is required") String postId) {
}

// ============================================================================
// RESPONSE DTOs
// ============================================================================

/**
 * Response DTO for post data.
 * Never includes sensitive information.
 */
public record PostResponse(
        String id,
        String title,
        String description,
        List<String> techStack,
        Boolean visibility,
        String authorId,
        String authorName,
        Integer commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    // Compact constructor for null-safe collections
    public PostResponse {
        if (techStack == null) {
            techStack = new ArrayList<>();
        }
    }
}

/**
 * Response DTO for user data.
 * NEVER expose password or other sensitive fields.
 */
public record UserResponse(
        String id,
        String email,
        String name,
        Role role,
        List<String> skills,
        LocalDateTime createdAt) {

    // Compact constructor for null-safe collections
    public UserResponse {
        if (skills == null) {
            skills = new ArrayList<>();
        }
    }
}

/**
 * Response DTO for authentication.
 * Contains JWT token and user information.
 */
public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user) {

    // Default token type
    public AuthResponse {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
}

/**
 * Response DTO for comment data.
 */
public record CommentResponse(
        String id,
        String content,
        String postId,
        String authorId,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

/**
 * Generic error response DTO.
 * Used by global exception handler.
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        String path) {

    // Constructor without path for backward compatibility
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, timestamp, null);
    }
}

/**
 * Response DTO for validation errors.
 * Contains field-specific error messages.
 */
public record ValidationErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        List<FieldError> errors) {

    public record FieldError(
            String field,
            String message) {
    }
}

// ============================================================================
// SUPPORTING TYPES
// ============================================================================

/**
 * Enum for user roles.
 */
enum Role {
    USER,
    ADMIN,
    MODERATOR
}

// ============================================================================
// BEST PRACTICES DEMONSTRATED
// ============================================================================

/*
 * ✅ DO:
 * - Use records for all DTOs (requests and responses)
 * - Add validation annotations directly on record components
 * - Use compact constructors for default values and null-safety
 * - Keep records immutable (don't add setters)
 * - Separate request and response DTOs
 * - Never expose sensitive data (passwords, tokens) in responses
 * - Use descriptive names (CreateXRequest, XResponse)
 * - Provide null-safe defaults for collections
 * 
 * ❌ DON'T:
 * - Don't use traditional classes with Lombok unless necessary
 * - Don't add business logic to records
 * - Don't expose entity objects directly as responses
 * - Don't reuse request DTOs as response DTOs
 * - Don't make records mutable
 * - Don't forget validation annotations
 * - Don't return null for collections (use empty lists)
 */
