package com.backend.devConnectBackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RegisterRequest(
        @NotBlank(message = "Name is required") String name,

        @NotBlank(message = "Email is required") @Email(message = "Need valid email") String email,

        @NotBlank(message = "Password is required") @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$", message = "Password must be at least 8 characters long and include 1 uppercase, 1 lowercase, and 1 number") String password,

        @NotBlank(message = "Role is required") String role,

        List<String> skills,

        @PositiveOrZero(message = "Current salary must be zero or positive") BigDecimal currentSalary,

        @PositiveOrZero(message = "Expected salary must be zero or positive") BigDecimal expectedSalary) {
    // Compact constructor to handle null skills with default empty list
    public RegisterRequest {
        if (skills == null) {
            skills = new ArrayList<>();
        }
    }
}
