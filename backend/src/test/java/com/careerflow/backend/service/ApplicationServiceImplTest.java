package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.ApplicationRequest;
import com.careerflow.backend.dto.response.ApplicationResponse;
import com.careerflow.backend.mapper.ApplicationMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.User;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.service.impl.ApplicationServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .build();

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(mockUser);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createApplication_ShouldReturnResponse_WhenRequestIsValid() {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google");

        Application applicationEntity = Application.builder()
                .id(1L)
                .companyName("Google")
                .build();

        ApplicationResponse expectedResponse = new ApplicationResponse();
        expectedResponse.setId(1L);
        expectedResponse.setCompanyName("Google");

        when(applicationMapper.toEntity(request)).thenReturn(applicationEntity);
        when(applicationRepository.save(any(Application.class))).thenReturn(applicationEntity);
        when(applicationMapper.toResponse(applicationEntity)).thenReturn(expectedResponse);

        ApplicationResponse actualResponse = applicationService.createApplication(request);

        assertNotNull(actualResponse);
        assertEquals("Google", actualResponse.getCompanyName());

        verify(applicationRepository).save(argThat(app -> app.getUser().equals(mockUser)));
    }
}