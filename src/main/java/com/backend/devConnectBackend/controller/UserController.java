package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.ProfileResult;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getCurrentUserProfile(@AuthenticationPrincipal User authenticatedUser) {
        String email = authenticatedUser.getEmail();

        ProfileResult result = userService.getCurrentUserProfile(email);

        if (result instanceof ProfileResult.ProfileNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        } else if (result instanceof ProfileResult.FullProfile full) {
            return ResponseEntity.ok(full);
        }

        throw new IllegalStateException("Unexpected ProfileResult type");
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<Object> getUserProfile(
            @PathVariable String id,
            @AuthenticationPrincipal User authenticatedUser) {
        String role = authenticatedUser.getRole().name();

        ProfileResult result = userService.getUserProfile(id, role);

        if (result instanceof ProfileResult.ProfileNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        } else if (result instanceof ProfileResult.FullProfile full) {
            return ResponseEntity.ok(full); // ADMIN viewing others
        } else if (result instanceof ProfileResult.PublicProfile pub) {
            return ResponseEntity.ok(pub); // Non-ADMIN viewing others
        }

        throw new IllegalStateException("Unexpected ProfileResult type");
    }
}
