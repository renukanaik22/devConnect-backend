package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.ProfileResult;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
                "123",
                "John Doe",
                "john@test.com",
                "hashedPassword",
                "USER",
                List.of("Java", "Spring"),
                new BigDecimal("50000.00"),
                new BigDecimal("60000.00"));
    }

    @Test
    void getUserProfile_AsAdmin_ReturnsFullProfile() {
        when(userRepository.findById("123")).thenReturn(Optional.of(testUser));

        ProfileResult result = userService.getUserProfile("123", "ADMIN");

        assertTrue(result instanceof ProfileResult.FullProfile);
        ProfileResult.FullProfile profile = (ProfileResult.FullProfile) result;
        assertEquals("123", profile.id());
        assertEquals("John Doe", profile.name());
        assertEquals("john@test.com", profile.email());
        assertEquals("USER", profile.role());
        assertEquals(List.of("Java", "Spring"), profile.skills());
        assertEquals(new BigDecimal("50000.00"), profile.currentSalary());
        assertEquals(new BigDecimal("60000.00"), profile.expectedSalary());
    }

    @Test
    void getUserProfile_AsUser_ReturnsPublicProfile() {
        when(userRepository.findById("123")).thenReturn(Optional.of(testUser));

        ProfileResult result = userService.getUserProfile("123", "USER");

        assertTrue(result instanceof ProfileResult.PublicProfile);
        ProfileResult.PublicProfile profile = (ProfileResult.PublicProfile) result;
        assertEquals("123", profile.id());
        assertEquals("John Doe", profile.name());
        assertEquals("john@test.com", profile.email());
        assertEquals("USER", profile.role());
        assertEquals(List.of("Java", "Spring"), profile.skills());
    }

    @Test
    void getUserProfile_NotFound_ReturnsProfileNotFound() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        ProfileResult result = userService.getUserProfile("999", "USER");

        assertTrue(result instanceof ProfileResult.ProfileNotFound);
    }

    @Test
    void getCurrentUserProfile_ReturnsFullProfile() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));

        ProfileResult result = userService.getCurrentUserProfile("john@test.com");

        assertTrue(result instanceof ProfileResult.FullProfile);
        ProfileResult.FullProfile profile = (ProfileResult.FullProfile) result;
        assertEquals("123", profile.id());
        assertEquals("John Doe", profile.name());
        assertEquals("john@test.com", profile.email());
        assertEquals("USER", profile.role());
        assertEquals(List.of("Java", "Spring"), profile.skills());
        assertEquals(new BigDecimal("50000.00"), profile.currentSalary());
        assertEquals(new BigDecimal("60000.00"), profile.expectedSalary());
    }

    @Test
    void getCurrentUserProfile_NotFound_ReturnsProfileNotFound() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        ProfileResult result = userService.getCurrentUserProfile("unknown@test.com");

        assertTrue(result instanceof ProfileResult.ProfileNotFound);
    }
}
