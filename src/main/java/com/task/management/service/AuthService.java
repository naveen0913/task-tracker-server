package com.task.management.service;

import com.task.management.DTO.LoginRequest;
import com.task.management.DTO.SignupRequest;
import com.task.management.DTO.UpdateRequest;
import com.task.management.config.JwtUtil;
import com.task.management.model.TaskImages;
import com.task.management.model.User;
import com.task.management.repository.UserRepository;
import com.task.management.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final MailService mailService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Map<String, Object> signup(SignupRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setCreated(LocalDateTime.now());
        user.setStatus(true);
        user.setRole("user");
        userRepository.save(user);
        mailService.sendAccountCreationMail(request.getEmail(), request.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 201);
        return response;
    }

    public Map<String, Object> login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.isStatus()) {
            throw new RuntimeException("Account is not active");
        }

        // Generate JWT
        String token = jwtUtil.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("token", token);
        response.put("data", user);
        return response;
    }

    @Transactional
    public ApiResponse changeAccountStatus(Long userId, boolean status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        return new ApiResponse<>(200, "updated", null);
    }

    @Transactional
    public ApiResponse updateUser(Long userId, UpdateRequest request, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getUsername() != null &&
                !request.getUsername().equals(user.getUsername())) {

            if (userRepository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }

        if (file != null && !file.isEmpty()) {
            saveProfileImage(user,file);
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        user.setUpdated(LocalDateTime.now());
        userRepository.save(user);
        return new ApiResponse<>(200, "updated", null);
    }

    private void saveProfileImage(User user, MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            user.setProfileUrl("/uploads/" + fileName);
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed");
        }
    }


    public ApiResponse deleteProfileImage(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete file from uploads folder
        if (user.getProfileUrl() != null) {
            File file = new File("uploads/" + user.getProfileUrl());
            if (file.exists()) {
                file.delete();
            }
            user.setProfileUrl(null);
            userRepository.save(user);
        }

        return new ApiResponse<>(200,"deleted",null);
    }

    public ApiResponse getAllUsers(){
        List<User> users = userRepository.findAll();
        return new ApiResponse<>(200,"users fetched",users);
    }

}
