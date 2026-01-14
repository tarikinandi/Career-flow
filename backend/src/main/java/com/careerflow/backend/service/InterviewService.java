package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.InterviewRequest;
import com.careerflow.backend.dto.response.InterviewResponse;

import java.util.List;

public interface InterviewService {

    InterviewResponse createInterview(Long applicationId, InterviewRequest request);
    List<InterviewResponse> getInterviewsByApplication(Long applicationId);
    void deleteInterview(Long id);
}
