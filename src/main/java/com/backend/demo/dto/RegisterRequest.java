package com.backend.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@JsonIgnoreProperties(ignoreUnknown = false)
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Need valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long and include 1 uppercase, 1 lowercase, and 1 number"
    )
    private String password;

    @NotBlank(message = "Role is required")
    private String role;

    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }
}
