package com.yatrasathi.service;

import com.yatrasathi.model.User;
import com.yatrasathi.repository.UserRepository;
import com.yatrasathi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public Map<String, Object> register(String name, String email, String password) {
        Map<String, Object> response = new HashMap<>();
        
        if (userRepository.findByEmail(email).isPresent()) {
            response.put("success", false);
            response.put("message", "User already exists");
            return response;
        }
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        
        response.put("success", true);
        response.put("message", "User registered successfully");
        return response;
    }
    
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "User not found");
            return response;
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return response;
        }
        
        String token = jwtUtil.generateToken(email);
        Map<String, String> userData = new HashMap<>();
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        
        response.put("success", true);
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", userData);
        return response;
    }
}