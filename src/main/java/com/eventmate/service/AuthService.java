package com.eventmate.service;

import com.eventmate.dto.AuthRequest;
import com.eventmate.dto.AuthResponse;
import com.eventmate.dto.RegisterRequest;
import com.eventmate.entity.User;
import com.eventmate.repository.UserRepository;
import com.eventmate.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User register(RegisterRequest req) {
        return userService.register(req);
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPassword()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
