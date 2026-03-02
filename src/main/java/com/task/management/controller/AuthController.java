package com.task.management.controller;

import com.task.management.DTO.LoginRequest;
import com.task.management.DTO.SignupRequest;
import com.task.management.model.User;
import com.task.management.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        Map<String,Object> response = authService.signup(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Map<String,Object> response = authService.login(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/authorized")
    public ResponseEntity<?> getLoggedUser(HttpServletRequest request) {

        User user = (User) request.getAttribute("loggedUser");

        if (user == null) {
            return ResponseEntity.status(401)
                    .body("Unauthorized");
        }

        return ResponseEntity.ok(user);
    }

}
