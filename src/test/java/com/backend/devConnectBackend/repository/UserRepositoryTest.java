package com.backend.devConnectBackend.repository;

import com.backend.devConnectBackend.model.Role;
import com.backend.devConnectBackend.model.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Disabled("Requires a running MongoDB instance. Enable if local MongoDB is available.")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFind_Success() {
        User user = new User(
                null,
                "TestRepo",
                "repo@test.com",
                "pass",
                Role.USER,
                java.util.List.of("MongoDB"),
                new BigDecimal("50000.00"),
                new BigDecimal("60000.00"));
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("repo@test.com");

        assertTrue(found.isPresent());
        assertEquals("TestRepo", found.get().getName());
    }
}