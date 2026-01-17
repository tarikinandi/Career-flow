package com.careerflow.backend.controller;

import com.careerflow.backend.dto.global.ApiResponse;
import com.careerflow.backend.dto.request.AuthenticationRequest;
import com.careerflow.backend.dto.request.RegisterRequest;
import com.careerflow.backend.dto.response.AuthenticationResponse;
import com.careerflow.backend.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authenticationService.register(request), "User registered successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authenticationService.authenticate(request), "User authenticated successfully"));
    }
}
