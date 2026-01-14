package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.LoginRequest;
import com.backend.devConnectBackend.dto.LoginResult;
import com.backend.devConnectBackend.dto.RegisterRequest;
import com.backend.devConnectBackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService service) {
        this.authService = service;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered!");
    }

    /**
     * Login endpoint with proper HTTP status codes.
     * Uses instanceof checks (Java 17 compatible).
     * 
     * @return 200 OK with JWT token on success
     *         404 NOT FOUND if user doesn't exist
     *         401 UNAUTHORIZED if password is incorrect
     */
    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest req) {
        LoginResult result = authService.login(req);

        // Handle each case with instanceof (Java 17 compatible)
        if (result instanceof LoginResult.Success success) {
            return ResponseEntity.ok(success.token());
        } else if (result instanceof LoginResult.UserNotFound) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        } else if (result instanceof LoginResult.InvalidPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid password");
        }

        // This should never happen due to sealed interface
        throw new IllegalStateException("Unexpected LoginResult type: " + result);
    }

}
// Frontend → backend service → repo → JWT → response.