package com.aiquizzer.service;

import com.aiquizzer.model.User;
import com.aiquizzer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    

    
    public User createUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameOrEmailContaining(keyword);
    }
    
    public User updateUser(Long id, String username, String email, String password) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check if new username is available
            if (username != null && !username.equals(user.getUsername())) {
                if (userRepository.existsByUsername(username)) {
                    throw new RuntimeException("Username already exists");
                }
                user.setUsername(username);
            }
            
            // Check if new email is available
            if (email != null && !email.equals(user.getEmail())) {
                if (userRepository.existsByEmail(email)) {
                    throw new RuntimeException("Email already exists");
                }
                user.setEmail(email);
            }
            
            // Update password if provided
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }
    
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public long getUserCount() {
        return userRepository.countAllUsers();
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
