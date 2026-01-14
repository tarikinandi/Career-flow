package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.ApplicationRequest;
import com.careerflow.backend.dto.response.ApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicationService {

    ApplicationResponse createApplication(ApplicationRequest request);
    Page<ApplicationResponse> getAllApplications(Pageable pageable);
    ApplicationResponse getApplicationById(Long id);
    ApplicationResponse updateApplication(Long id, ApplicationRequest request);
    void deleteApplication(Long id);
}
