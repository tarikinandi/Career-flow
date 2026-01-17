package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.InterviewRequest;
import com.careerflow.backend.dto.response.InterviewResponse;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.InterviewMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.Interview;
import com.careerflow.backend.model.User;
import com.careerflow.backend.model.enums.InterviewRound;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.repository.InterviewRepository;
import com.careerflow.backend.service.impl.InterviewServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id(1L).email("test@test.com").build();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(mockUser);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

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
        app.setUser(mockUser);

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