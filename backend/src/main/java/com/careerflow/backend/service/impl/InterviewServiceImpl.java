package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.request.InterviewRequest;
import com.careerflow.backend.dto.response.InterviewResponse;
import com.careerflow.backend.mapper.InterviewMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.Interview;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.InterviewRepository;
import com.careerflow.backend.service.InterviewService;
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
    public InterviewResponse createInterview(Long applicationId, InterviewRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found for this id : " + applicationId));
        Interview interview = interviewMapper.toEntity(request);
        interview.setApplication(application);

        Interview savedInterview = interviewRepository.save(interview);
        return interviewMapper.toResponse(interviewRepository.save(savedInterview));
    }

    @Override
    public List<InterviewResponse> getInterviewsByApplication(Long applicationId) {
        if (!applicationRepository.existsById(applicationId)){
            throw new RuntimeException("Application not found for this id : " + applicationId);
        }

        return interviewRepository.findAllByApplicationId(applicationId)
                .stream()
                .map(interviewMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void deleteInterview(Long id) {
        if (!interviewRepository.existsById(id)) {
            throw new RuntimeException("Interview not found for this id : " + id);
        }

        interviewRepository.deleteById(id);
    }
}
