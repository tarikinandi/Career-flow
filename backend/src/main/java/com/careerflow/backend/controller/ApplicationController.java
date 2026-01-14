package com.careerflow.backend.controller;

import com.careerflow.backend.dto.global.ApiResponse;
import com.careerflow.backend.dto.request.ApplicationRequest;
import com.careerflow.backend.dto.response.ApplicationResponse;
import com.careerflow.backend.mapper.ApplicationMapper;
import com.careerflow.backend.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponse>> createApplication(@Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.createApplication(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Application created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ApplicationResponse>>> getAllApplications(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ApplicationResponse> response = applicationService.getAllApplications(pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Applications fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationById(@PathVariable Long id) {
        ApplicationResponse response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Application details fetched"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateApplication(@PathVariable Long id, @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.updateApplication(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Application updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok(ApiResponse.success("Application deleted successfully"));
    }
}
