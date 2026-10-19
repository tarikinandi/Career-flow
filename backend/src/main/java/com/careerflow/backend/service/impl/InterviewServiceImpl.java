package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.request.InterviewRequest;
import com.careerflow.backend.dto.response.InterviewResponse;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.InterviewMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.Interview;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.InterviewRepository;
import com.careerflow.backend.service.InterviewService;
import com.careerflow.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewMapper interviewMapper;

    @Override
    @Transactional
    public InterviewResponse createInterview(Long applicationId, InterviewRequest request) {
        Application application = findOwnedApplication(applicationId);

        Interview interview = interviewMapper.toEntity(request);
        interview.setApplication(application);

        Interview savedInterview = interviewRepository.save(interview);
        return interviewMapper.toResponse(savedInterview);
    }

    @Override
    public List<InterviewResponse> getInterviewsByApplication(Long applicationId) {
        findOwnedApplication(applicationId);

        return interviewRepository.findAllByApplicationId(applicationId)
                .stream()
                .map(interviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteInterview(Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        Interview interview = interviewRepository.findByIdAndApplicationUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found for this id : " + id));
        interviewRepository.delete(interview);
    }

    /** Interviews are owned through their application; a foreign application is reported as missing (404). */
    private Application findOwnedApplication(Long applicationId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return applicationRepository.findByIdAndUserId(applicationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found for this id : " + applicationId));
    }
}
