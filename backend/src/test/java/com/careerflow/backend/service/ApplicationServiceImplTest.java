package com.careerflow.backend.service;

import com.careerflow.backend.dto.request.ApplicationRequest;
import com.careerflow.backend.dto.response.ApplicationResponse;
import com.careerflow.backend.mapper.ApplicationMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.enums.ApplicationStatus;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    ApplicationServiceImpl applicationService;

    @Test
    void createApplication_ShouldReturnResponse_WhenRequestIsValid(){
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google");

        Application applicationEntity = Application.builder().id(1L).companyName("Google").build();
        ApplicationResponse expectedResponse = new ApplicationResponse();
        expectedResponse.setId(1L);
        expectedResponse.setCompanyName("Google");

        when(applicationMapper.toEntity(request)).thenReturn(applicationEntity);
        when(applicationRepository.save(any(Application.class))).thenReturn(applicationEntity);
        when(applicationMapper.toResponse(applicationEntity)).thenReturn(expectedResponse);

        ApplicationResponse actualResponse = applicationService.createApplication(request);

        assertNotNull(actualResponse);
        assertEquals("Google", actualResponse.getCompanyName());

        verify(applicationRepository, times(1)).save(any(Application.class));
    }
}
