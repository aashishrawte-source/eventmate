package com.eventmate.service;

import com.eventmate.dto.RegisterRequest;
import com.eventmate.entity.User;
import com.eventmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email()))
            throw new RuntimeException("Email already registered");

        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(User.Role.valueOf(req.role().toUpperCase()))
                .build();

        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
