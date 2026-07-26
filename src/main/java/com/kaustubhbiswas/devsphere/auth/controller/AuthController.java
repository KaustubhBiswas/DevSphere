package com.kaustubhbiswas.devsphere.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaustubhbiswas.devsphere.auth.dto.request.LoginUserRequest;
import com.kaustubhbiswas.devsphere.auth.dto.request.RegisterUserRequest;
import com.kaustubhbiswas.devsphere.auth.dto.response.LoginUserResponse;
import com.kaustubhbiswas.devsphere.auth.dto.response.RegisterUserResponse;
import com.kaustubhbiswas.devsphere.auth.service.AuthService;
import com.kaustubhbiswas.devsphere.common.response.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ApiResponse<RegisterUserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        
        RegisterUserResponse response = authService.registerUser(request);

        return ApiResponse.success("User registered successfully.", response);
    }

    @PostMapping("/login")
    public ApiResponse<LoginUserResponse> loginUser(@Valid @RequestBody LoginUserRequest request){

        LoginUserResponse response = authService.loginUser(request);

        return ApiResponse.success("Login successful.", response);
    }

}
