package com.enterprise.taskmanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "User authentication APIs")
public class AuthController {

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        // Mock implementation for demo
        Map<String, Object> response = Map.of(
            "token", "mock-jwt-token",
            "user", Map.of(
                "id", "1",
                "username", credentials.get("username"),
                "email", credentials.get("username") + "@example.com",
                "role", "USER"
            )
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register new user account")
    public ResponseEntity<String> register(@RequestBody Map<String, String> userData) {
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh JWT token")
    public ResponseEntity<Map<String, String>> refreshToken() {
        return ResponseEntity.ok(Map.of("token", "new-mock-jwt-token"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user details")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Map<String, Object> user = Map.of(
            "id", "1",
            "username", "demo-user",
            "email", "demo@example.com",
            "role", "USER"
        );
        return ResponseEntity.ok(user);
    }
}