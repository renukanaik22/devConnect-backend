package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.LoginRequest;
import com.backend.devConnectBackend.dto.LoginResult;
import com.backend.devConnectBackend.dto.RegisterRequest;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.UserRepository;
import com.backend.devConnectBackend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private AuthService authService;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, jwtService);
    }

    @Test
    void register_Success() {
        RegisterRequest request = createRegisterRequest();
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = createLoginRequest();
        User user = new User(
                null,
                "John",
                "john@test.com",
                encoder.encode("password123"),
                "USER",
                java.util.List.of());
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("john@test.com", "USER")).thenReturn("jwt-token");

        LoginResult result = authService.login(request);

        assertTrue(result instanceof LoginResult.Success);
        assertEquals("jwt-token", ((LoginResult.Success) result).token());
        verify(jwtService).generateToken("john@test.com", "USER");
    }

    @Test
    void login_InvalidPassword() {
        LoginRequest request = createLoginRequest();
        User user = new User(
                null,
                "John",
                "john@test.com",
                encoder.encode("wrongpassword"),
                "USER",
                java.util.List.of());
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

        LoginResult result = authService.login(request);

        assertTrue(result instanceof LoginResult.InvalidPassword);
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_UserNotFound() {
        LoginRequest request = createLoginRequest();
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());

        LoginResult result = authService.login(request);

        assertTrue(result instanceof LoginResult.UserNotFound);
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    private RegisterRequest createRegisterRequest() {
        return new RegisterRequest(
                "John",
                "john@test.com",
                "password123",
                "USER",
                java.util.List.of("Java", "Spring"));
    }

    private LoginRequest createLoginRequest() {
        return new LoginRequest(
                "john@test.com",
                "password123");
    }
}