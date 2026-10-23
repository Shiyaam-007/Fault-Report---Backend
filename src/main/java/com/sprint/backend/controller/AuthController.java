package com.sprint.backend.controller;

import com.sprint.backend.dto.LoginRequest;
import com.sprint.backend.dto.LoginResponse;
import com.sprint.backend.dto.RegisterRequest;
import com.sprint.backend.entity.User;
import com.sprint.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        String username = request.getUsername() != null ? request.getUsername().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";

        // Query the users table for the given username (case-insensitive)
        Optional<User> optionalUser = userRepository.findByUsernameIgnoreCase(username);

        // Fallback matching if legacy data has no username stored
        if (optionalUser.isEmpty()) {
            if (username.equalsIgnoreCase("admin") || username.equalsIgnoreCase("bob")) {
                optionalUser = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == User.Role.ADMIN)
                        .findFirst();
            } else if (username.equalsIgnoreCase("student") || username.equalsIgnoreCase("alice")) {
                optionalUser = userRepository.findAll().stream()
                        .filter(u -> u.getRole() == User.Role.STUDENT)
                        .findFirst();
            }
        }

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not found. Please verify your username."));
        }

        User user = optionalUser.get();

        // Validate password against database
        boolean passwordMatches = false;
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            passwordMatches = user.getPassword().equals(password);
        } else {
            // Default password fallbacks if password was not set in DB
            if (user.getRole() == User.Role.ADMIN) {
                passwordMatches = password.equals("admin") || password.equals("admin123") || password.equals("password");
            } else {
                passwordMatches = password.equals("student") || password.equals("student123") || password.equals("password");
            }
        }

        if (!passwordMatches) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect password."));
        }

        LoginResponse response = LoginResponse.builder()
                .token(UUID.randomUUID().toString())
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername() != null ? user.getUsername() : username)
                .role(user.getRole().name())
                .message("Login successful")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        String username = request.getUsername().trim().toLowerCase();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Username '" + username + "' is already taken."));
        }

        User newUser = User.builder()
                .username(username)
                .password(request.getPassword().trim())
                .name(request.getName().trim())
                .role(User.Role.STUDENT)
                .build();

        User saved = userRepository.save(newUser);
        log.info("Registered new student user: {} ({})", saved.getName(), saved.getUsername());

        LoginResponse response = LoginResponse.builder()
                .token(UUID.randomUUID().toString())
                .id(saved.getId())
                .name(saved.getName())
                .username(saved.getUsername())
                .role(saved.getRole().name())
                .message("Account registered successfully")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

