package com.task.management.service;

import com.task.management.DTO.LoginRequest;
import com.task.management.DTO.SignupRequest;
import com.task.management.model.User;
import com.task.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public Map<String,Object> signup(SignupRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        userRepository.save(user);
        Map<String,Object> response = new HashMap<>();
        response.put("code",201);
        return response;
    }

    public Map<String,Object> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        Map<String,Object> response = new HashMap<>();
        response.put("code",200);
        response.put("data",user);
        return response;
    }

}
