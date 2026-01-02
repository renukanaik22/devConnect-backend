package com.backend.devConnectBackend.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_ValidInput() {
        String token = jwtService.generateToken("test@example.com", "USER");
        
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void generateToken_ContainsCorrectStructure() {
        String token = jwtService.generateToken("test@example.com", "ADMIN");
        
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length); // JWT has header.payload.signature
    }

    @Test
    void generateToken_DifferentInputsProduceDifferentTokens() {
        String token1 = jwtService.generateToken("user1@test.com", "USER");
        String token2 = jwtService.generateToken("user2@test.com", "ADMIN");
        
        assertNotEquals(token1, token2);
    }
}