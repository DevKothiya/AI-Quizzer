package com.aiquizzer.controller;

import com.aiquizzer.model.User;
import com.aiquizzer.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {
    @Autowired
    private  UserService userService;

    
    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        try {
            User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve a user by their username")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve a user by their email")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by username or email")
    public ResponseEntity<List<User>> searchUsers(
            @Parameter(description = "Search keyword") @RequestParam String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        try {
            User updatedUser = userService.updateUser(id, request.getUsername(), request.getEmail(), request.getPassword());
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user account")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/check-username/{username}")
    @Operation(summary = "Check username availability", description = "Check if a username is available")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(@PathVariable String username) {
        boolean available = !userService.existsByUsername(username);
        return ResponseEntity.ok(Map.of("available", available));
    }
    
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Check email availability", description = "Check if an email is available")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@PathVariable String email) {
        boolean available = !userService.existsByEmail(email);
        return ResponseEntity.ok(Map.of("available", available));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get user statistics", description = "Get overall user statistics")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = Map.of(
            "totalUsers", userService.getUserCount()
        );
        return ResponseEntity.ok(stats);
    }
    
    // DTOs for request/response
    @Data
    public static class RegisterUserRequest {
        private String username;
        private String email;
        private String password;

    }
    @Data
    public static class UpdateUserRequest {
        private String username;
        private String email;
        private String password;

    }
}
