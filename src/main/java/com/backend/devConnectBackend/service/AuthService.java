package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.LoginRequest;
import com.backend.devConnectBackend.dto.RegisterRequest;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.UserRepository;
import com.backend.devConnectBackend.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository repo;
    private final JwtService jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo, JwtService jwt) {
        this.repo = repo;
        this.jwt = jwt;
    }

    public String register(RegisterRequest request) {
        if (repo.findByEmail(request.getEmail()).isPresent()) {
            return "User already exists!";
        }

        String hashed = encoder.encode(request.getPassword());
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashed)
                .role(request.getRole())
                .skills(request.getSkills())
                .build();
        repo.save(user);
        return "User registered!";
    }

    public String login(LoginRequest req) {
        Optional<User> userOptional = repo.findByEmail(req.getEmail());

        if (userOptional.isEmpty()) {
            return "User not found!";
        }

        User user = userOptional.get();

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return "Invalid password!";
        }
        return jwt.generateToken(user.getEmail(), user.getRole());
    }
}
