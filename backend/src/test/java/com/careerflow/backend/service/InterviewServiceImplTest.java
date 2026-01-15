package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.InterviewRequest;
import com.careerflow.backend.dto.response.InterviewResponse;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.InterviewMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.Interview;
import com.careerflow.backend.model.enums.InterviewRound;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.InterviewRepository;
import com.careerflow.backend.service.impl.InterviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

    @Mock private InterviewRepository interviewRepository;
    @Mock private ApplicationRepository applicationRepository;
    @Mock private InterviewMapper interviewMapper;

    @InjectMocks
    private InterviewServiceImpl interviewService;

    @Test
    void addInterview_ShouldThrowException_WhenApplicationNotFound() {
        Long invalidAppId = 999L;
        InterviewRequest request = new InterviewRequest();

        when(applicationRepository.findById(invalidAppId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> interviewService.createInterview(invalidAppId, request)
        );

        verify(interviewRepository, never()).save(any());
    }

    @Test
    void addInterview_ShouldSave_WhenApplicationExists() {
        Long appId = 1L;
        Application app = new Application();
        app.setId(appId);

        InterviewRequest request = new InterviewRequest();
        request.setRound(InterviewRound.RECRUITER_SCREEN);

        Interview interview = new Interview();
        InterviewResponse response = new InterviewResponse();

        when(applicationRepository.findById(appId)).thenReturn(Optional.of(app));
        when(interviewMapper.toEntity(request)).thenReturn(interview);
        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewMapper.toResponse(interview)).thenReturn(response);

        interviewService.createInterview(appId, request);

        verify(interviewRepository).save(interview);
        assertEquals(app, interview.getApplication());
    }
}