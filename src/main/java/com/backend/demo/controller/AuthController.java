package com.backend.demo.controller;

import com.backend.demo.dto.LoginRequest;
import com.backend.demo.dto.RegisterRequest;
import com.backend.demo.service.AuthService;
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
    public String login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @GetMapping("/login-test")
    public String loginTest() {
        return "login-test ok";
    }
}
//Frontend → backend service → repo → JWT → response.