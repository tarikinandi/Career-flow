package com.careerflow.backend.service.impl;

import com.careerflow.backend.dto.request.ApplicationRequest;
import com.careerflow.backend.dto.response.ApplicationResponse;
import com.careerflow.backend.exception.ResourceNotFoundException;
import com.careerflow.backend.mapper.ApplicationMapper;
import com.careerflow.backend.model.Application;
import com.careerflow.backend.repository.ApplicationRepository;
import com.careerflow.backend.service.ApplicationService;
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
    public ApplicationResponse createApplication(ApplicationRequest request) {
        Application application = applicationMapper.toEntity(request);
        Application savedApplication = applicationRepository.save(application);
        return applicationMapper.toResponse(savedApplication);
    }

    @Override
    public Page<ApplicationResponse> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable)
                .map(applicationMapper::toResponse);
    }

    @Override
    public ApplicationResponse getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found for this id : " + id));

        return applicationMapper.toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplication(Long id, ApplicationRequest request) {
        Application existingApp = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found for this id : " + id));
        applicationMapper.updateEntityFromRequest(request, existingApp);

        Application updatedApp = applicationRepository.save(existingApp);
        return applicationMapper.toResponse(updatedApp);
    }

    @Override
    @Transactional
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found for this id : " + id);
        }
        applicationRepository.deleteById(id);
    }
}
