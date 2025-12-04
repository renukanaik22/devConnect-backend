package com.backend.demo.repository;

import com.backend.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class UserRepository {

    private final HashMap<String, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getEmail(), user);
    }

    public User findByEmail(String email) {
        return users.get(email);
    }
}
