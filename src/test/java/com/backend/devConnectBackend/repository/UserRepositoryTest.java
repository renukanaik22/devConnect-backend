package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
    }

    @Test
    void save_StoresUser() {
        User user = new User("John", "john@test.com", "hashedPassword", "USER");
        
        userRepository.save(user);
        
        User found = userRepository.findByEmail("john@test.com");
        assertEquals(user, found);
    }

    @Test
    void findByEmail_UserExists() {
        User user = new User("Jane", "jane@test.com", "hashedPassword", "ADMIN");
        userRepository.save(user);
        
        User found = userRepository.findByEmail("jane@test.com");
        
        assertNotNull(found);
        assertEquals("Jane", found.getName());
        assertEquals("jane@test.com", found.getEmail());
    }

    @Test
    void findByEmail_UserNotExists() {
        User found = userRepository.findByEmail("nonexistent@test.com");
        
        assertNull(found);
    }
}