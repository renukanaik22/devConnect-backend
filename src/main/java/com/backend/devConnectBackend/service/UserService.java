package com.backend.devConnectBackend.service;

import com.backend.devConnectBackend.dto.ProfileResult;
import com.backend.devConnectBackend.model.User;
import com.backend.devConnectBackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ProfileResult getUserProfile(String userId, String requestingUserRole) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return new ProfileResult.ProfileNotFound();
        }

        User user = userOptional.get();

        if ("ADMIN".equals(requestingUserRole)) {
            return new ProfileResult.FullProfile(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getSkills(),
                    user.getCurrentSalary(),
                    user.getExpectedSalary());
        } else {
            return new ProfileResult.PublicProfile(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getSkills());
        }
    }

    public ProfileResult getCurrentUserProfile(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return new ProfileResult.ProfileNotFound();
        }

        User user = userOpt.get();

        return new ProfileResult.FullProfile(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getSkills(),
                user.getCurrentSalary(),
                user.getExpectedSalary());
    }
}
