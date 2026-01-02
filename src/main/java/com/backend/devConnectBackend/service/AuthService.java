package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.LoginRequest;
import com.backend.devConnectBackend.dto.RegisterRequest;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.UserRepository;
import com.backend.devConnectBackend.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        String hashed = encoder.encode(request.getPassword());
        User user = new User(
                request.getName(),
                request.getEmail(),
                hashed,
                request.getRole()
        );
        repo.save(user);
        return "User registered!";
    }

    public String login(LoginRequest req) {
        User user = repo.findByEmail(req.getEmail());
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            return "Invalid password!";
        }
        return jwt.generateToken(user.getEmail(), user.getRole());
    }
}
