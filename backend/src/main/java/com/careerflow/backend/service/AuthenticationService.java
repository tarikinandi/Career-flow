package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.AuthenticationRequest;
import com.careerflow.backend.dto.request.RegisterRequest;
import com.careerflow.backend.dto.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
