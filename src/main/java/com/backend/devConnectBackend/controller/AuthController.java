package com.backend.devConnectBackend.controller;

import com.backend.devConnectBackend.dto.LoginRequest;
import com.backend.devConnectBackend.dto.RegisterRequest;
import com.backend.devConnectBackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService service) {
        this.authService = service;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/auth/login")
    public String login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

}
//Frontend → backend service → repo → JWT → response.