package com.task.management.config;

import com.task.management.model.User;
import com.task.management.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public User getUserFromRequest(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token missing");
        }

        String token = header.substring(7);

        String username = jwtUtil.extractUsername(token);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}