package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.request.ApplicationRequest;
import com.careerflow.backend.dto.response.ApplicationResponse;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.ApplicationMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.model.User;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.service.ApplicationService;
import com.careerflow.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;

    @Override
    @Transactional
    public ApplicationResponse createApplication(ApplicationRequest request) {
        User currentUser = SecurityUtils.getCurrentUser();

        Application application = applicationMapper.toEntity(request);

        application.setUser(currentUser);

        Application savedApplication = applicationRepository.save(application);
        return applicationMapper.toResponse(savedApplication);
    }

    @Override
    public Page<ApplicationResponse> getAllApplications(Pageable pageable) {
        User currentUser = SecurityUtils.getCurrentUser();
        return applicationRepository.findByUserId(currentUser.getId(), pageable)
                .map(applicationMapper::toResponse);
    }

    @Override
    public ApplicationResponse getApplicationById(Long id) {
        return applicationMapper.toResponse(findOwned(id));
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplication(Long id, ApplicationRequest request) {
        Application existingApp = findOwned(id);
        applicationMapper.updateEntityFromRequest(request, existingApp);

        Application updatedApp = applicationRepository.save(existingApp);
        return applicationMapper.toResponse(updatedApp);
    }

    @Override
    @Transactional
    public void deleteApplication(Long id) {
        applicationRepository.delete(findOwned(id));
    }

    /** Other users' applications are reported as missing (404), so their existence is not revealed. */
    private Application findOwned(Long id) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        return applicationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found for this id : " + id));
    }
}
