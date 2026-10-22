package com.sprint.backend.controller;

import com.sprint.backend.dto.LoginRequest;
import com.sprint.backend.dto.LoginResponse;
import com.sprint.backend.entity.User;
import com.sprint.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";

        // Standard accepted admin usernames & passwords
        boolean isUsernameValid = username.equalsIgnoreCase("admin")
                || username.equalsIgnoreCase("bob")
                || username.equalsIgnoreCase("bob admin")
                || username.equalsIgnoreCase("admin@campus.edu");

        boolean isPasswordValid = password.equals("admin")
                || password.equals("admin123")
                || password.equals("password")
                || password.equals("admin@123");

        if (!isUsernameValid || !isPasswordValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "Invalid admin credentials. Use admin / admin123"));
        }

        // Try to find the ADMIN user in DB (e.g., Bob Admin)
        Optional<User> adminUser = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ADMIN)
                .findFirst();

        Long id = adminUser.map(User::getId).orElse(2L);
        String name = adminUser.map(User::getName).orElse("Bob Admin");

        LoginResponse response = LoginResponse.builder()
                .token(UUID.randomUUID().toString())
                .id(id)
                .name(name)
                .username(username)
                .role("ADMIN")
                .message("Login successful")
                .build();

        return ResponseEntity.ok(response);
    }
}
