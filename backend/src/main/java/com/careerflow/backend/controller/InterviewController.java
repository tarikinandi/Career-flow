package com.careerflow.backend.controller;

import com.careerflow.backend.dto.global.ApiResponse;
import com.careerflow.backend.dto.request.InterviewRequest;
import com.careerflow.backend.dto.response.InterviewResponse;
import com.careerflow.backend.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/applications/{applicationId}/interviews")
    public ResponseEntity<ApiResponse<InterviewResponse>> addInterview(
            @PathVariable Long applicationId,
            @Valid @RequestBody InterviewRequest request) {
        InterviewResponse response = interviewService.createInterview(applicationId, request);
        return new ResponseEntity<>(ApiResponse.success(response, "Interview scheduled successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/applications/{applicationId}/interviews")
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> getInterviewsByApplication(@PathVariable Long applicationId) {
        List<InterviewResponse> response = interviewService.getInterviewsByApplication(applicationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Interviews fetched successfully"));
    }

    @DeleteMapping("/interviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInterview(@PathVariable Long id) {
        interviewService.deleteInterview(id);
        return ResponseEntity.ok(ApiResponse.success("Interview deleted successfully"));
    }
}
