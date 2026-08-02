package com.kaustubhbiswas.devsphere.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kaustubhbiswas.devsphere.auth.dto.request.LoginUserRequest;
import com.kaustubhbiswas.devsphere.auth.dto.request.RegisterUserRequest;
import com.kaustubhbiswas.devsphere.auth.dto.response.LoginUserResponse;
import com.kaustubhbiswas.devsphere.auth.dto.response.RegisterUserResponse;
import com.kaustubhbiswas.devsphere.auth.security.JwtService;
import com.kaustubhbiswas.devsphere.common.exception.BusinessValidationException;
import com.kaustubhbiswas.devsphere.user.Role;
import com.kaustubhbiswas.devsphere.user.User;
import com.kaustubhbiswas.devsphere.user.UserRepository;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterUserResponse registerUser(RegisterUserRequest request){

        if (!request.getPassword().equals(request.getConfirmPassword())){
            throw new BusinessValidationException("Passwords don't match.");
        }

        if (userRepository.existsByUsername(request.getUsername())){
            throw new BusinessValidationException("Username is already taken.");
        }

        if (userRepository.existsByEmail(request.getEmail())){
            throw new BusinessValidationException("Email is already registered.");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);

        RegisterUserResponse response = new RegisterUserResponse();

        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());

        return response;

    }

    public LoginUserResponse loginUser(LoginUserRequest request){
        
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BusinessValidationException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new BusinessValidationException("Invalid email or password.");
        }

        String token = jwtService.generateToken(user);

        LoginUserResponse response = new LoginUserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setToken(token);

        return response;

    }


}
